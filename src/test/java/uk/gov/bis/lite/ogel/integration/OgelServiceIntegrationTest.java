package uk.gov.bis.lite.ogel.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.ogel.OgelApplication;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class OgelServiceIntegrationTest {

  @ClassRule
  public static final WireMockRule wireMockRule = new WireMockRule(9000);

  @Rule
  public final DropwizardAppRule<MainApplicationConfiguration> RULE =
      new DropwizardAppRule<>(OgelApplication.class, resourceFilePath("service-test.yaml"));

  @BeforeClass
  public static void setUpMock() {
    stubFor(post(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("fixture/integration/spireResponse/getAllOgelsResponse.xml"))));
  }

  @Before
  public void setupDatabase() {
    DataSourceFactory f = RULE.getConfiguration().getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
    flyway.migrate();
  }

  @Test
  public void getAllOgelsSuccess() {
    await().with().pollInterval(1, SECONDS).atMost(30, SECONDS).until(() -> JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL0")
        .request()
        .get().hasEntity());
    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels")
        .request()
        .get();

    List<OgelFullView> actualResponse = response.readEntity(new GenericType<List<OgelFullView>>() {
    });
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(actualResponse.size()).isEqualTo(3);
    assertThat(actualResponse).extracting(ogel -> ogel.getId()).containsOnly("OGL0", "OGL1", "OGL2");

    verify(postRequestedFor(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .withRequestBody(equalToXml(fixture("fixture/integration/spireRequest/getAllOgelsRequest.xml"))));
  }

  @Test
  public void getOgelByIdSuccess() {
    await().with().pollInterval(1, SECONDS).atMost(30, SECONDS).until(() -> JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL0")
        .request()
        .get()
        .hasEntity());

    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL0")
        .request()
        .get();

    OgelFullView actualResponse = response.readEntity(OgelFullView.class);
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(actualResponse.getId()).isEqualTo("OGL0");
    assertThat(actualResponse.getSummary().getCanList()).isNotEmpty();

    verify(postRequestedFor(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .withRequestBody(equalToXml(fixture("fixture/integration/spireRequest/getAllOgelsRequest.xml"))));
  }

  @Test
  public void getOgelByIdOgelNotFoundException() {
    await().with().pollInterval(1, SECONDS).atMost(30, SECONDS).until(() -> JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL_")
        .request()
        .get().getStatus() == 404);

    Response response = JerseyClientBuilder.createClient()
        .target("http://localhost:8080/ogels/OGL_")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(404);

    verify(postRequestedFor(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .withRequestBody(equalToXml(fixture("fixture/integration/spireRequest/getAllOgelsRequest.xml"))));
  }

}
