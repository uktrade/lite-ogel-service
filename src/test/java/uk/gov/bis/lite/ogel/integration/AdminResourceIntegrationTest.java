package uk.gov.bis.lite.ogel.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.api.view.ValidateView;

import java.util.Arrays;

import javax.ws.rs.core.Response;

public class AdminResourceIntegrationTest extends BaseIntegrationTest {

  private static final String ADMIN_VALIDATE_URL = "http://localhost:8080/admin/validate";

  @Test
  public void validateSuccess() {
    // return all external control codes
    stubFor(get(urlEqualTo("/control-codes"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(fixture("fixture/integration/controlCode/controlCodesMatched.json"))));

    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    ValidateView actualResponse = response.readEntity(ValidateView.class);
    assertThat(actualResponse.getUnmatchedControlCodes().isEmpty()).isTrue();
    assertThat(actualResponse.getUnmatchedLocalOgelIds().isEmpty()).isTrue();
    assertThat(actualResponse.getUnmatchedSpireOgelIds().isEmpty()).isTrue();
  }

  @Test
  public void validateUnmatched() {
    // return all external control codes
    stubFor(get(urlEqualTo("/control-codes"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(fixture("fixture/integration/controlCode/controlCodesUnmatched.json"))));

    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .get();

    assertThat(response.getStatus()).isEqualTo(500);
    ValidateView actualResponse = response.readEntity(ValidateView.class);
    assertThat(actualResponse.getUnmatchedControlCodes()).isEqualTo(ImmutableMap.of("OGLZ", Arrays.asList("33")));
  }

  @Test
  public void validateUnauthorisedStatus() throws Exception {
    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.readEntity(String.class)).isEqualTo("Credentials are required to access this resource.");
  }

  @Test
  public void validateControlCodeFailure() throws Exception {
    // return all external control codes
    stubFor(get(urlEqualTo("/control-codes"))
        .willReturn(aResponse()
            .withStatus(500)));

    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .get();

    assertThat(response.getStatus()).isEqualTo(500);
    assertThat(response.readEntity(String.class)).isEqualTo("{\"code\":500,\"message\":\"Unable to get control code details from the control code service\"}");
  }

}
