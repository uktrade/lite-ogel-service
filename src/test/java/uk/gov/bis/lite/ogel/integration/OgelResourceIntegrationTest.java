package uk.gov.bis.lite.ogel.integration;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OgelResourceIntegrationTest extends BaseIntegrationTest {

  private static final String OGEL_URL = "http://localhost:8080/ogels/";

  @Test
  public void getAllOgelsSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    List<OgelFullView> actualResponse = response.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(actualResponse.size()).isEqualTo(4);
    assertThat(actualResponse).extracting(ogel -> ogel.getId())
        .containsOnly("OGLX", "OGLY", "OGLZ", "OGL61");
    assertThat(actualResponse).extracting(ogel -> ogel.getName())
        .containsOnly("NameOGLX", "NameOGLY", "NameOGLZ", "VirtualEuSpireNameOGL61");
    assertThat(actualResponse).extracting(ogel -> ogel.getSummary().getCanList())
        .containsOnly(Arrays.asList("CanList for OGLX"),Arrays.asList("CanList for OGLY"),Arrays.asList("CanList for OGLZ"),Arrays.asList());
  }

  @Test
  public void getOgelByIdSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGLX")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    OgelFullView actualResponse = response.readEntity(OgelFullView.class);
    assertThat(actualResponse.getId()).isEqualTo("OGLX");
    assertThat(actualResponse.getName()).isEqualTo("NameOGLX");
    assertThat(actualResponse.getSummary().getCanList()).isEqualTo(Arrays.asList("CanList for OGLX"));
    assertThat(actualResponse.getSummary().getCantList()).isEqualTo(Arrays.asList("CantList for OGLX"));
    assertThat(actualResponse.getSummary().getHowToUseList()).isEqualTo(null);
    assertThat(actualResponse.getSummary().getMustList()).isEqualTo(null);
  }

  @Test
  public void getOgelByIdOgelNotFoundException() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGL_")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(404);
    String expectedJson = "{\"code\":404,\"message\":\"No Ogel Found With Given Ogel ID: OGL_\"}";
    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
  }

  @Test
  public void updateOgelConditionSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGLX" + "/summary/canList")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(fixture("fixture/integration/updateOgelConditionRequest.json"), MediaType.APPLICATION_JSON_TYPE));

    assertThat(response.getStatus()).isEqualTo(202);
    OgelFullView actual = response.readEntity(OgelFullView.class);
    assertThat(actual.getId()).isEqualTo("OGLX");
    assertThat(actual.getName()).isEqualTo("NameOGLX");
    assertThat(actual.getSummary().getCanList()).isEqualTo(Arrays.asList("update canList with some text"));
  }

  @Test
  public void updateOgelConditionOgelNotFound() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL_" + "/summary/canList")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(fixture("fixture/integration/updateOgelConditionRequest.json"), MediaType.APPLICATION_JSON_TYPE));

    assertThat(response.getStatus()).isEqualTo(404);
    String expectedJson = "{\"code\":404,\"message\":\"No Ogel Found With Given Ogel ID: OGL_\"}";
    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
  }

  @Test
  public void insertOrUpdateOgelSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGLX")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.localX()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(201);
    OgelFullView actual = response.readEntity(OgelFullView.class);
    assertThat(actual.getId()).isEqualTo("OGLX");
    assertThat(actual.getName()).isEqualTo("NameOGLX");
    assertThat(actual.getSummary().getCanList()).isEqualTo(Arrays.asList("can1", "can2", "can3"));
    assertThat(actual.getSummary().getCantList()).isEqualTo(Arrays.asList("cannot1", "cannot2"));
    assertThat(actual.getSummary().getMustList()).isEqualTo(Arrays.asList("must1", "must2"));
    assertThat(actual.getSummary().getHowToUseList()).isEqualTo(Arrays.asList("how1", "how2"));
  }

  @Test
  public void insertOrUpdateOgelNotFound() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGL_")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.localX()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(404);
    String expectedJson = "{\"code\":404,\"message\":\"No Ogel Found With Given Ogel ID: OGL_\"}";
    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
  }

  @Test
  public void insertOgelArraySuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.getLocalOgels()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(201);
    List<OgelFullView> actual = response.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(actual).extracting(ogel -> ogel.getId()).containsOnly("OGLX", "OGLY");
  }

  @Test
  public void insertOgelArrayValidationMissingOgelId() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.getLocalOgelsMissingOgelId()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(422);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"errors\":[\"The request body Local Ogel Without ID is not allowed! Index: 1\"]}");
  }

  @Test
  public void insertOgelArrayEmptyOgel() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((Arrays.asList()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"code\":400,\"message\":\"Empty Ogel List\"}");
  }

  @Test
  public void deleteAllOgelsSuccess() {
    //before delete
    Response response1 = JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .get();

    assertThat(response1.getStatus()).isEqualTo(200);
    List<OgelFullView> actualResponse1 = response1.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(actualResponse1.size()).isEqualTo(4);
    assertThat(actualResponse1).extracting(ogel -> ogel.getId()).containsOnly("OGLX", "OGLY", "OGLZ", "OGL61");
    assertThat(actualResponse1).extracting(ogel -> ogel.getName()).containsOnly("NameOGLX", "NameOGLY", "NameOGLZ", "VirtualEuSpireNameOGL61");
    assertThat(actualResponse1).extracting(ogel -> ogel.getSummary().getCanList())
        .containsOnly(Arrays.asList("CanList for OGLX"),Arrays.asList("CanList for OGLY"),Arrays.asList("CanList for OGLZ"),Arrays.asList());

    // delete ogel
    JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .delete();

    //after delete
    Response response2 = JerseyClientBuilder.createClient()
        .target(OGEL_URL)
        .request()
        .get();

    assertThat(response2.getStatus()).isEqualTo(200);
    List<String> emptyCanList = new ArrayList<>();
    List<OgelFullView> actualResponse2 = response2.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(actualResponse2.size()).isEqualTo(4);
    assertThat(actualResponse2).extracting(ogel -> ogel.getId()).containsOnly("OGLX", "OGLY", "OGLZ", "OGL61");
    assertThat(actualResponse2).extracting(ogel -> ogel.getName()).containsOnly("SpireNameOGLX", "SpireNameOGLY", "SpireNameOGLZ", "VirtualEuSpireNameOGL61");
    assertThat(actualResponse2).extracting(ogel -> ogel.getSummary().getCanList())
        .containsOnly(emptyCanList, emptyCanList, emptyCanList, emptyCanList);
  }

  @Test
  public void deleteOgelByIdSuccess() {
    //before delete
    Response response1 = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGLX")
        .request()
        .get();

    assertThat(response1.getStatus()).isEqualTo(200);
    OgelFullView actualResponse1 = response1.readEntity(OgelFullView.class);
    assertThat(actualResponse1.getId()).isEqualTo("OGLX");
    assertThat(actualResponse1.getName()).isEqualTo("NameOGLX");
    assertThat(actualResponse1.getSummary().getCanList()).isEqualTo(Arrays.asList("CanList for OGLX"));
    assertThat(actualResponse1.getSummary().getCantList()).isEqualTo(Arrays.asList("CantList for OGLX"));

    // delete ogel
    JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGLX")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .delete();

    //after delete
    Response response2 = JerseyClientBuilder.createClient()
        .target(OGEL_URL+"OGLX")
        .request()
        .get();

    assertThat(response2.getStatus()).isEqualTo(200);
    OgelFullView actualResponse2 = response2.readEntity(OgelFullView.class);
    assertThat(actualResponse2.getId()).isEqualTo("OGLX");
    assertThat(actualResponse2.getName()).isEqualTo("SpireNameOGLX");
    assertThat(actualResponse2.getSummary().getCanList()).isEqualTo(Arrays.asList());
    assertThat(actualResponse2.getSummary().getCantList()).isEqualTo(Arrays.asList());
  }
}
