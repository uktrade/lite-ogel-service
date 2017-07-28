//package uk.gov.bis.lite.ogel.integration;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertEquals;
//
//import org.glassfish.jersey.client.JerseyClientBuilder;
//import org.junit.Test;
//import org.skyscreamer.jsonassert.JSONAssert;
//import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
//import uk.gov.bis.lite.ogel.api.view.OgelFullView;
//import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
//import uk.gov.bis.lite.ogel.util.TestUtil;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.GenericType;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//public class ControlCodeConditionsResourceIntegrationTest extends BaseIntegrationTest {
//
//  @Test
//  public void insertOgelConditionsArraySuccess() {
//    Response response = JerseyClientBuilder.createClient()
//        .target("http://localhost:8080/control-code-conditions")
//        .request()
//        .header("Authorization", "Basic dXNlcjpwYXNz")
//        .put(Entity.entity(Arrays.asList(buildControlCodeCondition()), MediaType.APPLICATION_JSON));
//
//    assertThat(response.getStatus()).isEqualTo(201);
//    List<LocalControlCodeCondition> actual = response.readEntity(new GenericType<List<LocalControlCodeCondition>>() {
//    });
//    assertThat(actual).extracting(controlCodeCondition -> controlCodeCondition.getControlCode()).containsOnly("ML22a");
//    assertThat(actual).extracting(controlCodeCondition -> controlCodeCondition.getOgelID()).containsOnly("OGLZ");
//    assertThat(actual).extracting(controlCodeCondition -> controlCodeCondition.getConditionDescription()).containsOnly("Goods");
//  }
//
//  @Test
//  public void insertOgelConditionsArrayOgelNotFound() {
//    LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
//    controlCodeCondition.setOgelID("OGL_");
//    controlCodeCondition.setControlCode("ML22a");
//
//    Response response = JerseyClientBuilder.createClient()
//        .target("http://localhost:8080/control-code-conditions")
//        .request()
//        .header("Authorization", "Basic dXNlcjpwYXNz")
//        .put(Entity.entity(Arrays.asList(controlCodeCondition), MediaType.APPLICATION_JSON));
//
//    assertThat(response.getStatus()).isEqualTo(404);
//    String expectedJson = "{\"code\":404,\"message\":\"No Ogel Found With Given Ogel ID: OGL_\"}";
//    JSONAssert.assertEquals(expectedJson, response.readEntity(String.class), false);
//  }
//
//  @Test
//  public void getControlCodeConditionByIdSuccess() {
//    Response response = JerseyClientBuilder.createClient()
//        .target("http://localhost:8080/control-code-conditions/OGLZ/ML22a")
//        .request()
//        .get();
//
//    assertEquals(200, response.getStatus());
//    ControlCodeConditionFullView actualResponse = response.readEntity(ControlCodeConditionFullView.class);
//    assertThat(actualResponse.getOgelId()).isEqualTo("OGLZ");
//    assertThat(actualResponse.getControlCode()).isEqualTo("ML22a");
//  }
//
//  private LocalControlCodeCondition buildControlCodeCondition() {
//    return buildControlCodeCondition(new ArrayList<>());
//  }
//
//  private LocalControlCodeCondition buildControlCodeCondition(List<String> descriptions) {
//    LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
//    controlCodeCondition.setOgelID("OGLZ");
//    controlCodeCondition.setControlCode("ML22a");
//    controlCodeCondition.setConditionDescription("Goods");
//    controlCodeCondition.setConditionDescriptionControlCodes(descriptions);
//    return controlCodeCondition;
//  }
//}
