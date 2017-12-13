package uk.gov.bis.lite.ogel.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.integration.BaseIntegrationTest;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import javax.ws.rs.core.Response;

public class ApplicableOgelResourceAuthTest extends BaseIntegrationTest {

  private static final String APPLICABLE_OGELS_URL = "http://localhost:8080/applicable-ogels";

  private static final String CONTROL_CODE = "ML22a";
  private static final String SOURCE_COUNTRY = "39";
  private static final String DESTINATION_COUNTRY = "TestDestination";
  private static final String ACTIVITY_TYPE = "TECH";

  @Test
  public void shouldReturnOkForAdminUser() {
    Response response = JerseyClientBuilder.createClient()
        .target(APPLICABLE_OGELS_URL)
        .queryParam("controlCode", CONTROL_CODE)
        .queryParam("sourceCountry", SOURCE_COUNTRY)
        .queryParam("destinationCountry", DESTINATION_COUNTRY)
        .queryParam("activityType", ACTIVITY_TYPE)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void shouldReturnUnauthorisedForNoAuthHeader() {
    Response response = JerseyClientBuilder.createClient()
        .target(APPLICABLE_OGELS_URL)
        .queryParam("controlCode", CONTROL_CODE)
        .queryParam("sourceCountry", SOURCE_COUNTRY)
        .queryParam("destinationCountry", DESTINATION_COUNTRY)
        .queryParam("activityType", ACTIVITY_TYPE)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void shouldReturnUnauthorisedForUnknownUser() {
    for (String user : AuthUtil.UNKNOWN_USERS) {
      Response response = JerseyClientBuilder.createClient()
          .target(APPLICABLE_OGELS_URL)
          .queryParam("controlCode", CONTROL_CODE)
          .queryParam("sourceCountry", SOURCE_COUNTRY)
          .queryParam("destinationCountry", DESTINATION_COUNTRY)
          .queryParam("activityType", ACTIVITY_TYPE)
          .request()
          .header(AuthUtil.HEADER, user)
          .get();

      assertThat(response.getStatus()).isEqualTo(401);
    }
  }

}
