package uk.gov.bis.lite.ogel.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import uk.gov.bis.lite.ogel.OgelApplication;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;

import java.io.IOException;

public class BaseIntegrationTest {

  private static EmbeddedPostgres postgres;
  /**
   * Manually starting WireMockRule and DropwizardAppRule as WireMockRule is configured to use dynamic port allocation due
   * to a bug with WireMocks' Jetty configuration.
   * TODO convert to @ClassRule WireMockClassRule when https://github.com/tomakehurst/wiremock/issues/97 is resolved.
   */
  private WireMockRule wireMockRule;

  private DropwizardAppRule<MainApplicationConfiguration> appRule;

  @BeforeClass
  public static void pgSetup() throws IOException {
    postgres = new EmbeddedPostgres(V9_5);
    postgres.start("localhost", 5432, "dbName", "postgres", "password");
  }

  @Before
  public void setUp() {
    // WireMock setup
    wireMockRule = new WireMockRule(options().dynamicPort());
    wireMockRule.start();

    // configures stubFor to use allocated port
    configureFor("localhost", wireMockRule.port());

    stubFor(post(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("fixture/integration/spire/getAllOgelsResponse.xml"))));

    // Dropwizard setup overriding
    appRule = new DropwizardAppRule<>(OgelApplication.class, "service-test.yaml",
        ConfigOverride.config("controlCodeServiceUrl", "http://localhost:" + wireMockRule.port() + "/"),
        ConfigOverride.config("spireClientUrl", "http://localhost:" + wireMockRule.port() + "/spire/fox/ispire/"));
    appRule.getTestSupport().before();

    // await Spire OGEL cache load
    await().with().pollInterval(1, SECONDS).atMost(20, SECONDS).until(() -> JerseyClientBuilder.createClient()
        .target("http://localhost:" + appRule.getAdminPort() + "/ready")
        .request()
        .get()
        .getStatus() == 200);
  }

  @After
  public void tearDown() throws Exception {
    // Delete all migrations so next test run resets them all
    DataSourceFactory f = appRule.getConfiguration().getDatabase();
    Flyway flyway = new Flyway();
    flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
    flyway.clean();

    wireMockRule.stop();
    appRule.getTestSupport().after();
    wireMockRule = null;
    appRule = null;
  }

  @AfterClass
  public static void pgStop() {
    postgres.stop();
  }

}
