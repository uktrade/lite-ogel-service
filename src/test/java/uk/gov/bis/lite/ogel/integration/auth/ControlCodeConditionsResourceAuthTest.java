package uk.gov.bis.lite.ogel.integration.auth;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.integration.BaseIntegrationTest;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class ControlCodeConditionsResourceAuthTest extends BaseIntegrationTest {

  private static final String CONTROL_CODE_CONDITIONS_URL = "http://localhost:8080/control-code-conditions";
  private static final String OGEL_ID = "OGLZ";
  private static final String CONTROL_CODE = "33";

  // GET

  @Test
  public void getShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getShouldReturnUnauthorisedForUnknownUsers() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(CONTROL_CODE_CONDITIONS_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // GET BY ID

  @Test
  public void getByIdShouldReturnOkForAdminUser() {
    stubForBulkControlCodes();
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL + "/" + OGEL_ID + "/" + CONTROL_CODE)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getByIdShouldReturnUnauthorisedForNoAuthHeader() {
    stubForBulkControlCodes();
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL + "/" + OGEL_ID + "/" + CONTROL_CODE)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getByIdShouldReturnUnauthorisedForUnknownUsers() {
    stubForBulkControlCodes();
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(CONTROL_CODE_CONDITIONS_URL + "/" + OGEL_ID + "/" + CONTROL_CODE)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // PUT

  @Test
  public void putShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .put(Entity.json(buildLocalControlCodeConditions()));

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void putShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .put(Entity.json(buildLocalControlCodeConditions()));

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void putShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(CONTROL_CODE_CONDITIONS_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .put(Entity.json(buildLocalControlCodeConditions()));

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // DELETE

  @Test
  public void deleteShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .delete();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void deleteShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONTROL_CODE_CONDITIONS_URL)
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void deleteShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(CONTROL_CODE_CONDITIONS_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .delete();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  private List<LocalControlCodeCondition> buildLocalControlCodeConditions() {
    LocalControlCodeCondition localControlCodeCondition = new LocalControlCodeCondition();
    localControlCodeCondition.setOgelID(OGEL_ID);
    localControlCodeCondition.setControlCode("55");
    localControlCodeCondition.setConditionDescription("New ConditionDesc for OGLZ");
    localControlCodeCondition.setConditionDescriptionControlCodes(new ArrayList<>());
    return Collections.singletonList(localControlCodeCondition);
  }

  private void stubForBulkControlCodes() {
    stubFor(get(urlEqualTo("/bulk-control-codes?controlCode=33"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(fixture("fixture/integration/controlCode/bulkControlCodes.json"))));
  }

}
