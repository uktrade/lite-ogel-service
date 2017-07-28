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

  @Test
  public void validateSuccess() {
    // return all external control codes
    stubFor(get(urlEqualTo("/control-codes"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(fixture("fixture/integration/controlCodeResponse/allControlCodesMatched.json"))));

    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/admin/validate")
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
            .withBody(fixture("fixture/integration/controlCodeResponse/allControlCodesUnmatched.json"))));

    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/admin/validate")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .get();

    assertThat(response.getStatus()).isEqualTo(500);
    ValidateView actualResponse = response.readEntity(ValidateView.class);
    assertThat(actualResponse.getUnmatchedControlCodes()).isEqualTo(ImmutableMap.of("OGLZ", Arrays.asList("33")));
  }
}
