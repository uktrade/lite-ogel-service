package uk.gov.bis.lite.ogel.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.api.view.ValidateView;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import javax.ws.rs.core.Response;

public class AdminResourceIntegrationTest extends BaseIntegrationTest {

  private static final String ADMIN_VALIDATE_URL = "http://localhost:8080/admin/validate";

  @Test
  public void validateSuccess() {
    Response response = JerseyClientBuilder.createClient()
        .target(ADMIN_VALIDATE_URL)
        .request()
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    ValidateView actualResponse = response.readEntity(ValidateView.class);
    assertThat(actualResponse.getUnmatchedLocalOgelIds()).isEmpty();
    assertThat(actualResponse.getUnmatchedSpireOgelIds()).isEmpty();
  }

}
