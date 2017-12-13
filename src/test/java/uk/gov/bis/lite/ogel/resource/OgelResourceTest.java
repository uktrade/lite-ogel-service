package uk.gov.bis.lite.ogel.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.AuthUtil;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

public class OgelResourceTest {
  private final SpireOgelService spireService = Mockito.mock(SpireOgelService.class);
  private final LocalOgelService localService = Mockito.mock(LocalOgelService.class);

  private final LocalOgel logel = TestUtil.localX();
  private final SpireOgel ogel = TestUtil.ogelX();

  @Rule
  public final ResourceTestRule resources = AuthUtil.authBuilder()
      .addResource(new OgelResource(spireService, localService))
      .build();

  @Test
  public void putOgelsSuccess() {
    when(spireService.findSpireOgelById(anyString())).thenReturn(Optional.of(ogel));

    Response response = resources.client().target("/ogels")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(TestUtil.getLocalOgels()));
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

  @Test
  public void putOgelsMissingOgelId() {
    Response response = resources.client()
        .target("/ogels")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(TestUtil.getLocalOgelsMissingOgelId()));
    assertEquals(422, response.getStatus());
  }

  @Test()
  public void putOgelsDuplicate() {
    Response response = resources.client()
        .target("/ogels")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(TestUtil.getLocalOgelsDuplicate()));
    assertEquals(422, response.getStatus());
  }

  @Test
  public void putOgelsInvalidData() {
    Response response = resources.client()
        .target("/ogels")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(TestUtil.getLocalOgelsInvalid()));
    assertEquals(422, response.getStatus());
  }

  @Test
  public void putOgelNotFoundException() {
    when(spireService.findSpireOgelById(anyString())).thenReturn(Optional.empty());

    Response response = resources.client()
        .target("/ogels")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(TestUtil.getOgelsNotFound()));

    assertEquals(404, response.getStatus());
  }

  @Test
  public void allOgels() {
    when(localService.findLocalOgelById(anyString())).thenReturn(Optional.empty());
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));

    Response response = resources.client().target("/ogels")
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertEquals(200, response.getStatus());
    List<OgelFullView> views = getOgelFullViews(response);
    assertEquals(1, views.size());
    assertEquals(TestUtil.OGLX, views.get(0).getId());
  }

  @Test
  public void expectedSpireOgel() throws SOAPException, XPathExpressionException, IOException {
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    when(spireService.findSpireOgelById(anyString())).thenReturn(Optional.of(ogel));
    when(localService.findLocalOgelById(anyString())).thenReturn(Optional.of(logel));

    final Response response = resources.client().target("/ogels/" + TestUtil.OGLX)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    JsonNode node = new ObjectMapper().readTree(response.readEntity(String.class));
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

    final Response response = resources.client().target("/ogels/" + TestUtil.OGL_)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertEquals(404, response.getStatus());
  }

  @Test
  public void localOgelNotFound() {
    when(spireService.findSpireOgelById(anyString())).thenReturn(Optional.of(TestUtil.ogelX()));
    when(localService.findLocalOgelById((anyString()))).thenReturn(Optional.empty());

    Response response = resources.client().target("/ogels/" + TestUtil.OGL_)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertEquals(200, response.getStatus());
  }

  @Test
  public void updateOgelConditionOgelNotFound() {
    when(spireService.findSpireOgelById(TestUtil.OGL_NF)).thenReturn(Optional.empty());
    when(localService.findLocalOgelById((TestUtil.OGL_NF))).thenReturn(Optional.empty());

    Response response = resources.client().target("/ogels/" + TestUtil.OGL_NF + "/summary/canList")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(Collections.singletonList("update canList with some text")));

    assertEquals(404, response.getStatus());
  }

  @Test
  public void updateOgelConditionSuccess() {
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    when(spireService.findSpireOgelById(anyString())).thenReturn(Optional.of(ogel));
    when(localService.findLocalOgelById(anyString())).thenReturn(Optional.of(logel));

    Response response = resources.client().target("/ogels/" + TestUtil.OGLX + "/summary/canList")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(Collections.singletonList("update canList with some text")));

    assertEquals(202, response.getStatus());
  }

  @Test
  public void deleteAllOgels() throws Exception {

    Response result = resources.client().target("/ogels")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .delete();

    assertThat(result.getStatus()).isEqualTo(204);
    verify(localService).deleteAllOgels();
  }

  @Test
  public void deleteOgelById() throws Exception {

    Response result = resources.client().target("/ogels/OGL1")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .delete();

    assertThat(result.getStatus()).isEqualTo(204);
    verify(localService).deleteOgelById("OGL1");
  }

  @Test
  public void insertInvalidOgel() {
    Response response = resources.client().target("/ogels/" + TestUtil.OGLTEMP)
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(TestUtil.invalidOgel()));

    assertEquals(422, response.getStatus());
  }

  @Test
  public void insertOrUpdateRequestIsHandledCorrectly() throws SQLException {
    when(spireService.getAllOgels()).thenReturn(Collections.singletonList(ogel));
    when(spireService.findSpireOgelById(anyString())).thenReturn(Optional.of(TestUtil.ogelX()));
    when(localService.insertOrUpdateOgel(any(LocalOgel.class))).thenReturn(logel);
    when(localService.findLocalOgelById(anyString())).thenReturn(Optional.of(logel));

    Response response = resources.client().target("/ogels/" + TestUtil.OGLX)
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(logel));

    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

  private List<OgelFullView> getOgelFullViews(Response response) {
    return response.readEntity(new GenericType<List<OgelFullView>>() {});
  }

}
