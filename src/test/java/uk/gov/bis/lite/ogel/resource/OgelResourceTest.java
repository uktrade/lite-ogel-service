package uk.gov.bis.lite.ogel.resource;

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
import uk.gov.bis.lite.ogel.model.OgelFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

public class OgelResourceTest {
  private SpireOgelService spireService = Mockito.mock(SpireOgelService.class);
  private LocalOgelService localService = Mockito.mock(LocalOgelService.class);

  private LocalOgel logel = new LocalOgel();
  private SpireOgel ogel = new SpireOgel();

  private HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("username", "password");

  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new OgelResource(spireService, localService))
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
    this.ogel = TestUtil.ogelX();
    this.logel = TestUtil.localX();
  }

  @Test
  public void allOgels() {
    when(localService.getAllLocalOgels()).thenReturn(Collections.singletonList(logel));
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    Response response = resources.client().target("/ogels").request().get();
    assertEquals(200, response.getStatus());
    List<OgelFullView> views = getOgelFullViews(response);
    assertEquals(1, views.size());
    assertEquals(TestUtil.OGLX, getMapped(views).get("id"));
  }

  @Test
  public void expectedSpireOgel() throws SOAPException, XPathExpressionException, IOException {
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    when(spireService.findSpireOgelById(anyString())).thenReturn(ogel);
    when(localService.findLocalOgelById(anyString())).thenReturn(logel);
    final Response response = resources.client().target("/ogels/" + TestUtil.OGLX).request().get();

    JsonNode node = getResponseNode(response);
    assertEquals(TestUtil.OGLX, node.get("id").asText());
    assertEquals(3, node.get("summary").get("canList").size());
    assertEquals("can2", node.get("summary").get("canList").get(1).asText());
    assertEquals("cannot1", node.get("summary").get("cantList").get(0).asText());
    assertEquals(2, node.get("summary").get("mustList").size());
  }

  @Test
  public void ogelNotFoundException() {
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    when(spireService.findSpireOgelById(anyString())).thenThrow(new OgelNotFoundException(TestUtil.OGL_));
    final Response response = resources.client().target("/ogels/" + TestUtil.OGL_).request().get();
    assertEquals(404, response.getStatus());
  }

  @Test
  public void localOgelNotFound() {
    when(spireService.findSpireOgelById(anyString())).thenReturn(TestUtil.ogelX());
    when(localService.findLocalOgelById((anyString()))).thenReturn(null);
    Response response = resources.client().target("/ogels/" + TestUtil.OGL_).request().get();
    assertEquals(200, response.getStatus());
  }

  @Test
  public void insertInvalidOgel() {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setLink("link");
    Response response = resources.client().register(feature).target("/ogels/" + TestUtil.OGLTEMP)
        .request(MediaType.APPLICATION_JSON).put(Entity.entity(TestUtil.invalidOgel(), MediaType.APPLICATION_JSON));
    assertEquals(400, response.getStatus());
  }

  @Test
  public void insertOrUpdateRequestIsHandledCorrectly() throws SQLException {
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    when(spireService.findSpireOgelById(anyString())).thenReturn(TestUtil.ogelX());
    when(localService.insertOrUpdateOgel(any(LocalOgel.class))).thenReturn(logel);
    Response response = resources.client().register(feature).target("/ogels/" + TestUtil.OGLX)
        .request(MediaType.APPLICATION_JSON).put(Entity.entity(logel, MediaType.APPLICATION_JSON));
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

  private JsonNode getResponseNode(Response response) {
    JsonNode node = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      node = objectMapper.readTree(response.readEntity(String.class));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return node;
  }

  private Map getMapped(List<OgelFullView> views) {
    return (Map) views.get(0);
  }
  private List<OgelFullView> getOgelFullViews(Response response) {
    return (List<OgelFullView>) response.readEntity(List.class);
  }

  private static class TestAuthenticator implements Authenticator<BasicCredentials, PrincipalImpl> {
    @Override
    public Optional<PrincipalImpl> authenticate(BasicCredentials credentials) throws AuthenticationException {
      return Optional.of(new PrincipalImpl("testAuth"));
    }
  }
}
