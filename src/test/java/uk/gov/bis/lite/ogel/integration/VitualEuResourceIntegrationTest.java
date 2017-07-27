package uk.gov.bis.lite.ogel.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.ogel.api.view.VirtualEuView;

import javax.ws.rs.core.Response;

public class VitualEuResourceIntegrationTest extends BaseIntegrationTest {

  @Test
  public void getVirtualEuTrue() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/virtual-eu")
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "3399")
        .request()
        .get();

    assertEquals(200, response.getStatus());
    VirtualEuView actualResponse = response.readEntity(VirtualEuView.class);
    assertThat(actualResponse.getOgelId()).isEqualTo("OGL61");
    assertThat(actualResponse.isVirtualEu()).isEqualTo(true);
  }

  @Test
  public void getVirtualEuFalse() {
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/virtual-eu")
        .queryParam("controlCode", "ML22a")
        .queryParam("sourceCountry", "39")
        .queryParam("destinationCountry", "4499")
        .request()
        .get();

    assertEquals(200, response.getStatus());
    VirtualEuView actualResponse = response.readEntity(VirtualEuView.class);
    assertThat(actualResponse.getOgelId()).isEqualTo(null);
    assertThat(actualResponse.isVirtualEu()).isEqualTo(false);
  }
}
