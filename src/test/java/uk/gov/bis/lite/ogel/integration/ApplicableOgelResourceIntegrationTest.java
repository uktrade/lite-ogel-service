package uk.gov.bis.lite.ogel.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.gov.bis.lite.ogel.api.view.ApplicableOgelView;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class ApplicableOgelResourceIntegrationTest extends BaseIntegrationTest {

  private static final String APPLICABLE_OGELS_URL = "http://localhost:8080/applicable-ogels";

  @Test
  public void getOgelListSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(APPLICABLE_OGELS_URL)
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "TestDestination")
        .queryParam("activityType", "TECH")
        .request()
        .get();

    assertEquals(200, response.getStatus());
    List<ApplicableOgelView> actualResponse = response.readEntity(new GenericType<List<ApplicableOgelView>>() {
    });
    assertEquals(1, actualResponse.size());
    assertThat(actualResponse).extracting(ogel -> ogel.getId()).containsOnly("OGLX");
    assertThat(actualResponse).extracting(ogel -> ogel.getName()).containsOnly("NameOGLX");;
    assertThat(actualResponse).flatExtracting(ogel -> ogel.getUsageSummary()).containsOnly("CanList for OGLX");
  }

  @Test
  public void getOgelListInvalidActivityType() {
    Response response = JerseyClientBuilder.createClient()
        .target(APPLICABLE_OGELS_URL)
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "TestCountry")
        .queryParam("activityType", "TECH_")
        .request()
        .get();

    assertEquals(400, response.getStatus());
    String expectedJson = "{\"code\":400,\"message\":\"Invalid activityType: TECH_\"}";
    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
  }

  @Test
  public void getOgelListMissingDestinationCountry() {
    Response response = JerseyClientBuilder.createClient()
        .target(APPLICABLE_OGELS_URL)
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("activityType", "TECH_")
        .request()
        .get();

    assertEquals(400, response.getStatus());
    String expectedJson = "{\"code\":400,\"message\":\"At least one destinationCountry must be provided\"}";
    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
  }

  @Test
  public void getOgelListMissingActivityType() {
    Response response = JerseyClientBuilder.createClient()
        .target(APPLICABLE_OGELS_URL)
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "TestCountry")
        .request()
        .get();

    assertEquals(400, response.getStatus());
    String expectedJson = "{\"code\":400,\"message\":\"At least one activityType must be provided\"}";
    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
  }
}
