package uk.gov.bis.lite.ogel.integration;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.util.TestUtil;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OgelResourceIntegrationTest extends BaseIntegrationTest {

  @Test
  public void getAllOgelsSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    List<OgelFullView> actualResponse = response.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(actualResponse.size()).isEqualTo(3);
    assertThat(actualResponse).extracting(ogel -> ogel.getId()).containsOnly("OGLX", "OGLY", "OGLZ");
  }

  @Test
  public void getOgelByIdSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGLX")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    OgelFullView actualResponse = response.readEntity(OgelFullView.class);
    assertThat(actualResponse.getId()).isEqualTo("OGLX");
  }

  @Test
  public void getOgelByIdOgelNotFoundException() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL_")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  public void updateOgelConditionSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGLX" + "/summary/canList")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(fixture("fixture/integration/updateOgelConditionRequest.json"), MediaType.APPLICATION_JSON_TYPE));

    assertThat(response.getStatus()).isEqualTo(202);
    OgelFullView actual = response.readEntity(OgelFullView.class);
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
  }

  @Test
  public void insertOrUpdateOgelSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGLX")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.localX()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(201);
    OgelFullView actual = response.readEntity(OgelFullView.class);
    assertThat(actual.getSummary().getCanList()).isEqualTo(Arrays.asList("can1", "can2", "can3"));
    assertThat(actual.getSummary().getCantList()).isEqualTo(Arrays.asList("cannot1", "cannot2"));
    assertThat(actual.getSummary().getMustList()).isEqualTo(Arrays.asList("must1", "must2"));
    assertThat(actual.getSummary().getHowToUseList()).isEqualTo(Arrays.asList("how1", "how2"));
  }

  @Test
  public void insertOrUpdateOgelNotFound() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL_")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.localX()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  public void insertOgelArraySuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity((TestUtil.getLocalOgels()), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(201);
    List<OgelFullView> actual = response.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(actual).extracting(ogel -> ogel.getId()).containsOnly("OGLX", "OGLY");
  }
}
