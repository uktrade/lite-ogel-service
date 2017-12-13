package uk.gov.bis.lite.ogel.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.integration.BaseIntegrationTest;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import javax.ws.rs.core.Response;

public class VirtualEuResourceAuthTest extends BaseIntegrationTest {

  private static final String VIRTUAL_EU_URL = "http://localhost:8080/virtual-eu";

  @Test
  public void getShouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(VIRTUAL_EU_URL)
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "3399")
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void getShouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(VIRTUAL_EU_URL)
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "3399")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void getShouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(VIRTUAL_EU_URL)
          .queryParam("controlCode", "ML22a")
          .queryParam("sourceCountry", "39")
          .queryParam("destinationCountry", "3399")
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

}
