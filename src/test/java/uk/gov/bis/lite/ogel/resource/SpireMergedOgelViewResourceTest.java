package uk.gov.bis.lite.ogel.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundException;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

public class SpireMergedOgelViewResourceTest {

  private static final SpireOgelService ogelSpireService = Mockito.mock(SpireOgelService.class);
  private static final LocalOgelService ogelLocalService = Mockito.mock(LocalOgelService.class);
  private LocalOgel localOgel = new LocalOgel();
  private SpireOgel spireOgel = new SpireOgel();

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new SpireMergedOgelViewResource(ogelSpireService, ogelLocalService))
      .addResource(new OgelNotFoundException.OgelNotFoundExceptionHandler())
      .addResource(new LocalOgelNotFoundException.LocalOgelNotFoundExceptionHandler())
      .addResource(new AuthDynamicFeature(
          new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
              .setAuthenticator(new TestAuthenticator())
              .setRealm("Basic Dropwizard Http Authentication")
              .buildAuthFilter()))
      .addResource(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class))
      .build();

  @Before
  public void setUp(){
    localOgel.setId("OGL1");
    OgelConditionSummary summary = new OgelConditionSummary();
    summary.setCanList(Arrays.asList("cando1", "cando2", "cando3"));
    summary.setCantList(Arrays.asList("illegal1", "illegal2"));
    summary.setMustList(Arrays.asList("mustdo1", "mustdo2"));
    summary.setHowToUseList(Arrays.asList("howtoUse1", "howtoUse2"));
    localOgel.setSummary(summary);

    spireOgel.setId("OGL1");
    spireOgel.setCategory(CategoryType.REPAIR);
  }

  @Test
  public void getsExpectedSpireOgel() throws SOAPException, XPathExpressionException, IOException {
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyListOf(SpireOgel.class), anyString())).thenCallRealMethod();
    when(ogelLocalService.findLocalOgelById(anyString())).thenReturn(localOgel);
    final Response response = resources.client().target("/ogel/OGL1").request().get();
    ObjectMapper objectMapper = new ObjectMapper();
    //jackson won't auto deserialize the custom OgelFullView object
    final JsonNode responseJsonNode = objectMapper.readTree(response.readEntity(String.class));
    assertEquals("OGL1", responseJsonNode.get("id").asText());
    assertEquals(3, responseJsonNode.get("summary").get("canList").size());
    assertEquals("cando2", responseJsonNode.get("summary").get("canList").get(1).asText());
    assertEquals("illegal1", responseJsonNode.get("summary").get("cantList").get(0).asText());
    assertEquals(2, responseJsonNode.get("summary").get("mustList").size());
  }

  @Test
  public void OgelNotFoundExceptionIsHandled() {
    when(ogelSpireService.findSpireOgelById(anyListOf(SpireOgel.class), anyString())).thenCallRealMethod();
    final Response response = resources.client().target("/ogel/invalid").request().get();
    assertEquals(404, response.getStatus());
    assertEquals("No Ogel Found With Given Ogel ID: invalid", response.readEntity(String.class));
  }

  @Test
  public void LocalOgelNotFoundExceptionIsHandled() throws LocalOgelNotFoundException {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setId("OGL1");
    spireOgel.setCategory(CategoryType.REPAIR);
    when(ogelSpireService.findSpireOgelById(anyListOf(SpireOgel.class), anyString())).thenReturn(spireOgel);
    when(ogelLocalService.findLocalOgelById((anyString()))).thenThrow(new LocalOgelNotFoundException("unknown"));
    Response response = resources.client().target("/ogel/unknown").request().get();
    assertEquals(500, response.getStatus());
    assertEquals("An unexpected error occurred. Failed to find local OGEL entry with ID: unknown", response.readEntity(String.class));
  }

  @Test
  public void insertOrUpdateRequestIsHandledCorrectly() throws JsonProcessingException {
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyListOf(SpireOgel.class), anyString())).thenCallRealMethod();
    when(ogelLocalService.insertOrUpdateOgel(any(LocalOgel.class))).thenReturn(localOgel);
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("username", "password");
    Response response = resources.client().register(feature)
        .target("/ogel/edit/OGL2").request(MediaType.APPLICATION_JSON).put(Entity.entity(localOgel, MediaType.APPLICATION_JSON));
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

  private static class TestAuthenticator implements Authenticator<BasicCredentials, PrincipalImpl> {
    @Override
    public Optional<PrincipalImpl> authenticate(BasicCredentials credentials) throws AuthenticationException {
      return Optional.of(new PrincipalImpl("testAuth"));
    }
  }
}
