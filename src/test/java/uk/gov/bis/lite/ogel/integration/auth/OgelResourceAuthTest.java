package uk.gov.bis.lite.ogel.integration.auth;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.integration.BaseIntegrationTest;
import uk.gov.bis.lite.ogel.util.AuthUtil;
import uk.gov.bis.lite.ogel.util.TestUtil;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class OgelResourceAuthTest extends BaseIntegrationTest {

  private static final String OGELS_URL = "http://localhost:8080/ogels";
  private static final String BY_ID_URL = OGELS_URL + "/OGLX";
  private static final String CONDITION_URL = OGELS_URL + "/OGLX/summary/canList";

  // GET

  @Test
  public void getShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGELS_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGELS_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(OGELS_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // GET BY ID

  @Test
  public void getByIdShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(BY_ID_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getByIdShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(BY_ID_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getByIdShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(BY_ID_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // PUT CONDITION

  @Test
  public void putConditionShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONDITION_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .put(Entity.json(fixture("fixture/integration/updateOgelConditionRequest.json")));

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void putConditionShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(CONDITION_URL)
        .request()
        .put(Entity.json(fixture("fixture/integration/updateOgelConditionRequest.json")));

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void putConditionShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(CONDITION_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .put(Entity.json(fixture("fixture/integration/updateOgelConditionRequest.json")));

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // PUT BY ID

  @Test
  public void putByIdShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(BY_ID_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .put(Entity.json(TestUtil.localX()));

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void putByIdShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(BY_ID_URL)
        .request()
        .put(Entity.json(TestUtil.localX()));

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void putByIdShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(BY_ID_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .put(Entity.json(TestUtil.localX()));

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // DELETE

  @Test
  public void deleteShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGELS_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .delete();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void deleteShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(OGELS_URL)
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void deleteShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(OGELS_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .delete();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

  // DELETE BY ID

  @Test
  public void deleteByIdShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(BY_ID_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .delete();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void deleteByIdShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(BY_ID_URL)
        .request()
        .delete();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void deleteByIdShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(BY_ID_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .delete();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

}
