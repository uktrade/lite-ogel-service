package uk.gov.bis.lite.ogel.integration;

import static org.junit.Assert.assertEquals;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.gov.bis.lite.ogel.api.view.ApplicableOgelView;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class ApplicableOgelResourceIntegrationTest extends BaseIntegrationTest {

  @Test
  public void getOgelListSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/applicable-ogels")
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "TestCountry")
        .queryParam("activityType", "TECH")
        .request()
        .get();

    assertEquals(200, response.getStatus());
    List<ApplicableOgelView> actualResponse = response.readEntity(new GenericType<List<ApplicableOgelView>>() {
    });
    assertEquals(1, actualResponse.size());
  }

  @Test
  public void getOgelListInvalidActivityType() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/applicable-ogels")
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
}
