package uk.gov.bis.lite.ogel.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ControlCodeConditionsResourceIntegrationTest extends BaseIntegrationTest {

  private static final String CONTROL_CODE_CONDITIONS_URL = "http://localhost:8080/control-code-conditions/";
  private static final String OGEL_ID = "OGLZ";
  private static final String CONTROL_CODE = "33";

  @Test
  public void getAllControlCodeConditionSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .get();

    assertEquals(200, response.getStatus());

    List<LocalControlCodeCondition> actualResponse = response.readEntity(new GenericType<List<LocalControlCodeCondition>>() {
    });
    assertThat(actualResponse.size()).isEqualTo(3);
    assertThat(actualResponse).extracting(controlCodeCondition -> controlCodeCondition.getOgelID())
        .containsOnly("OGLX", "OGLY", "OGLZ");
    assertThat(actualResponse).extracting(controlCodeCondition -> controlCodeCondition.getControlCode())
        .containsOnly("11", "22", "33");
  }

  @Test
  public void getControlCodeConditionByIdSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL + OGEL_ID + "/33")
        .request()
        .get();

    assertEquals(200, response.getStatus());
    ControlCodeConditionFullView actualResponse = response.readEntity(ControlCodeConditionFullView.class);
    assertThat(actualResponse.getOgelId()).isEqualTo("OGLZ");
    assertThat(actualResponse.getControlCode()).isEqualTo("33");
  }

  @Test
  public void getControlCodeConditionByIdBulkControlCode() {
    // return all external control codes
    stubFor(get(urlEqualTo("/bulk-control-codes?controlCode=33"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(fixture("fixture/integration/controlCode/bulkControlCodes.json"))));

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL + OGEL_ID + "/33")
        .request()
        .get();

    assertEquals(200, response.getStatus());
    ControlCodeConditionFullView actualResponse = response.readEntity(ControlCodeConditionFullView.class);
    assertThat(actualResponse.getOgelId()).isEqualTo("OGLZ");
    assertThat(actualResponse.getControlCode()).isEqualTo("33");
    assertThat(actualResponse.getConditionDescriptionControlCodes().getControlCodes().get(0).getControlCode()).isEqualTo("33");
    assertThat(actualResponse.getConditionDescriptionControlCodes().getControlCodes().get(0).getFriendlyDescription()).isEqualTo("FriendlyDescription");
    assertThat(actualResponse.getConditionDescription()).isEqualTo("ConditionDesc for OGLZ");
  }

  @Test
  public void getControlCodeConditionByIdPartialContent() {
    // return all external control codes
    stubFor(get(urlEqualTo("/bulk-control-codes?controlCode=33"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(fixture("fixture/integration/controlCode/bulkControlCodesMissingControlCode.json"))));

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL + OGEL_ID + "/33")
        .request()
        .get();

    assertEquals(206, response.getStatus());
    ControlCodeConditionFullView actualResponse = response.readEntity(ControlCodeConditionFullView.class);
    assertThat(actualResponse.getConditionDescriptionControlCodes().getMissingControlCodes()).isEqualTo(Arrays.asList("33"));
    assertThat(actualResponse.getConditionDescriptionControlCodes().getControlCodes().isEmpty()).isTrue();
  }

  @Test
  public void getControlCodeConditionByIdNoContent() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL + OGEL_ID + "/32")
        .request()
        .get();

    assertEquals(204, response.getStatus());
  }

  @Test
  public void insertOgelConditionsArraySuccess() {
    LocalControlCodeCondition localControlCodeCondition = new LocalControlCodeCondition();
    localControlCodeCondition.setOgelID(OGEL_ID);
    localControlCodeCondition.setControlCode("55");
    localControlCodeCondition.setConditionDescription("Goods");
    localControlCodeCondition.setConditionDescriptionControlCodes(new ArrayList<>());

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(Arrays.asList(localControlCodeCondition), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(201);
    List<LocalControlCodeCondition> actual = response.readEntity(new GenericType<List<LocalControlCodeCondition>>() {
    });
    assertThat(actual).extracting(controlCodeCondition -> controlCodeCondition.getControlCode()).contains("55");
    assertThat(actual).extracting(controlCodeCondition -> controlCodeCondition.getOgelID()).contains(OGEL_ID);
    assertThat(actual).extracting(controlCodeCondition -> controlCodeCondition.getConditionDescription()).contains("Goods");
  }

  @Test
  public void insertOgelConditionsArrayOgelNotFound() {
    LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
    controlCodeCondition.setOgelID("OGL_");
    controlCodeCondition.setControlCode("22");

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(Arrays.asList(controlCodeCondition), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(404);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"code\":404,\"message\":\"No Ogel Found With Given Ogel ID: OGL_\"}");
  }

  @Test
  public void insertOgelConditionsArrayValidateOgel() {
    LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
    controlCodeCondition.setControlCode("22");

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(Arrays.asList(controlCodeCondition), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(422);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"errors\":[\"The request body OGEL Control Code Condition without OGEL ID is not allowed, Index: 0\"]}");
  }

  @Test
  public void insertOgelConditionsArrayValidateControlCode() {
    LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
    controlCodeCondition.setOgelID("OGLX");

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(Arrays.asList(controlCodeCondition), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(422);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"errors\":[\"The request body OGEL Control Code Condition without Control Code is not allowed, Index: 0\"]}");
  }

  @Test
  public void insertOgelConditionsArrayValidateDuplicate() {
    LocalControlCodeCondition controlCodeCondition1 = new LocalControlCodeCondition();
    controlCodeCondition1.setOgelID("OGLX");
    controlCodeCondition1.setControlCode("55");
    LocalControlCodeCondition controlCodeCondition2 = new LocalControlCodeCondition();
    controlCodeCondition2.setOgelID("OGLX");
    controlCodeCondition2.setControlCode("55");

    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .put(Entity.entity(Arrays.asList(controlCodeCondition1,controlCodeCondition2), MediaType.APPLICATION_JSON));

    assertThat(response.getStatus()).isEqualTo(422);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"errors\":[\"The request body Duplicate OGEL Control Code Conditions found in bulk update data: OGLX/55\"]}");
  }

  @Test
  public void deleteControlCodeConditionSuccess() {
    //before delete
    Response response1 = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .get();

    List<LocalControlCodeCondition> actualResponse1 = response1.readEntity(new GenericType<List<LocalControlCodeCondition>>() {
    });
    assertThat(actualResponse1.size()).isEqualTo(3);
    assertThat(actualResponse1).extracting(controlCodeCondition -> controlCodeCondition.getOgelID())
        .containsOnly("OGLX", "OGLY", "OGLZ");

    // delete all local ControlCodeConditions
    JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .delete();

    //after delete
    Response response2 = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .get();

    List<LocalControlCodeCondition> actualResponse2 = response2.readEntity(new GenericType<List<LocalControlCodeCondition>>() {
    });
    assertThat(actualResponse2.size()).isEqualTo(0);
    assertThat(actualResponse2.isEmpty()).isTrue();
  }
}
