package uk.gov.bis.lite.ogel.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import uk.gov.bis.lite.ogel.OgelApplication;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;

public class BaseIntegrationTest {

  public WireMockRule wireMockRule;

  public DropwizardAppRule<MainApplicationConfiguration> RULE;

  @Before
  public void setUp() {
    wireMockRule = new WireMockRule(options().port(9000));
    wireMockRule.start();
    wireMockRule.stubFor(post(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("fixture/integration/spire/getAllOgelsResponse.xml"))));

    RULE = new DropwizardAppRule<>(OgelApplication.class, resourceFilePath("service-test.yaml"),
        ConfigOverride.config("controlCodeServiceUrl", "http://localhost:" +  wireMockRule.port() + "/"),
        ConfigOverride.config("spireClientUrl", "http://localhost:" +  wireMockRule.port() + "/spire/fox/ispire/"));

    // Start Dropwizard
    RULE.getTestSupport().before();

    DataSourceFactory f = RULE.getConfiguration().getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
    flyway.migrate();

    // Wait until Dropwizard is ready
    await().with().pollInterval(1, SECONDS).atMost(10, SECONDS).until(() -> {
      boolean ready = JerseyClientBuilder.createClient()
              .target("http://localhost:"+RULE.getAdminPort()+"/ready")
              .request()
              .get()
              .getStatus() == 200;
      System.out.println("Dropwizard starting: ready = " + ready);
      return ready;
    });
  }

  @After
  public void tearDown() throws Exception {
    // Stop Dropwizard
    RULE.getTestSupport().after();

    //Stop WireMock
    wireMockRule.stop();
  }
}
