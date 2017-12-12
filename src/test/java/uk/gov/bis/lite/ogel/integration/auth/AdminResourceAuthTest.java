package uk.gov.bis.lite.ogel.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.integration.BaseIntegrationTest;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import javax.ws.rs.core.Response;

public class AdminResourceAuthTest extends BaseIntegrationTest {

  private static final String ADMIN_VALIDATE_URL = "http://localhost:8080/admin/validate";

  @Test
  public void validateShouldReturnForbiddenForServiceUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void validateShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void validateShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(ADMIN_VALIDATE_URL)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

}
