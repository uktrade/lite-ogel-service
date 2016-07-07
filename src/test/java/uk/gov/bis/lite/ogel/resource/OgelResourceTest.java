package uk.gov.bis.lite.ogel.resource;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

public class OgelResourceTest {
  private SpireOgelService ogelSpireService = Mockito.mock(SpireOgelService.class);
  private LocalOgelService ogelLocalService = Mockito.mock(LocalOgelService.class);
  private LocalOgel localOgel = new LocalOgel();
  private SpireOgel spireOgel = new SpireOgel();
  private HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("username", "password");

  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new OgelResource(ogelSpireService, ogelLocalService))
      .addResource(new OgelNotFoundException.OgelNotFoundExceptionHandler())
      .addResource(new AuthDynamicFeature(
          new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
              .setAuthenticator(new TestAuthenticator())
              .setRealm("Basic Dropwizard Http Authentication")
              .buildAuthFilter()))
      .addResource(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class))
      .build();

  @Before
  public void setUp() {
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
  public void getAllOgels() {
    when(ogelLocalService.getAllLocalOgels()).thenReturn(Collections.singletonList(localOgel));
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelByIdOrReturnNull("OGL1")).thenCallRealMethod();
    Response response = resources.client().target("/ogels").request().get();
    assertEquals(200, response.getStatus());
    List allOgels = response.readEntity(List.class);
    Map returnedHashMap = (Map) allOgels.get(0);
    assertEquals(spireOgel.getId(), returnedHashMap.get("id"));
    assertEquals(4, ((Map) returnedHashMap.get("summary")).size());
  }

  @Test
  public void getsExpectedSpireOgel() throws SOAPException, XPathExpressionException, IOException {
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyString())).thenReturn(spireOgel);
    when(ogelLocalService.findLocalOgelById(anyString())).thenReturn(localOgel);
    final Response response = resources.client().target("/ogels/OGL1").request().get();
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
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyString())).thenCallRealMethod();
    final Response response = resources.client().target("/ogels/invalid").request().get();
    assertEquals(404, response.getStatus());
    assertEquals("No Ogel Found With Given Ogel ID: invalid", response.readEntity(String.class));
  }

  @Test
  public void LocalOgelNotFoundCaseIsHandled() {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setId("OGL1");
    spireOgel.setCategory(CategoryType.REPAIR);
    when(ogelSpireService.findSpireOgelById(anyString())).thenReturn(spireOgel);
    when(ogelLocalService.findLocalOgelById((anyString()))).thenReturn(null);
    Response response = resources.client().target("/ogels/unknown").request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void insertOrUpdateRequestIsHandledCorrectly() throws SQLException {
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyString())).thenCallRealMethod();
    when(ogelLocalService.insertOrUpdateOgel(any(LocalOgel.class))).thenReturn(localOgel);
    Response response = resources.client().register(feature)
        .target("/ogels/OGL1").request(MediaType.APPLICATION_JSON).put(Entity.entity(localOgel, MediaType.APPLICATION_JSON));
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

  @Test
  public void insertOrUpdateMissingSpireOgel() {
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyString())).thenCallRealMethod();
    Response response = resources.client().register(feature)
        .target("/ogels/OGL2").request(MediaType.APPLICATION_JSON).put(Entity.entity(localOgel, MediaType.APPLICATION_JSON));
    assertNotNull(response);
    assertEquals(500, response.getStatus());
    assertEquals("No Ogel Found With Given Ogel ID: OGL2", response.readEntity(String.class));
  }

  @Test
  public void insertOgelWithInvalidField() {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setLink("link");
    Response response = resources.client().register(feature)
        .target("/ogels/OGL2").request(MediaType.APPLICATION_JSON).put(Entity.entity(spireOgel, MediaType.APPLICATION_JSON));
    assertEquals(400, response.getStatus());
    assertEquals("Invalid or empty property name found in the request json", response.readEntity(String.class));
  }

  private static class TestAuthenticator implements Authenticator<BasicCredentials, PrincipalImpl> {
    @Override
    public Optional<PrincipalImpl> authenticate(BasicCredentials credentials) throws AuthenticationException {
      return Optional.of(new PrincipalImpl("testAuth"));
    }
  }


}
