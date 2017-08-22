package uk.gov.bis.lite.ogel.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import uk.gov.bis.lite.ogel.OgelApplication;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;

public class BaseIntegrationTest {

  @ClassRule
  public static final WireMockClassRule wireMockClassRule = new WireMockClassRule(9000);

  @Rule
  public WireMockClassRule wireMockRule = wireMockClassRule;

  @ClassRule
  public static final DropwizardAppRule<MainApplicationConfiguration> RULE =
      new DropwizardAppRule<>(OgelApplication.class, resourceFilePath("service-test.yaml"));

  @Before
  public void setUp() {
    // Start WireMock
    wireMockRule.start();
    wireMockRule.stubFor(post(urlEqualTo("/spire/fox/ispire/SPIRE_OGEL_TYPES"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("fixture/integration/spire/getAllOgelsResponse.xml"))));

    // Wait until WireMock is running
    await().with().pollInterval(50, MILLISECONDS).atMost(10, SECONDS).until(() -> {
      boolean running = wireMockRule.isRunning();
      System.out.println("WireMock starting: running = " + running);
      return running;
    });

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

    await().with().pollInterval(50, MILLISECONDS).atMost(10, SECONDS).until(() -> {
      boolean running = RULE.getTestSupport().getEnvironment().getApplicationContext().isRunning();
      System.out.println("Dropwizard stopping: running = " + running);
      return !running;
    });

    //Stop WireMock
    wireMockRule.stop();
    wireMockRule.resetAll();

    await().with().pollInterval(50, MILLISECONDS).atMost(10, SECONDS).until(() -> {
      boolean running = wireMockRule.isRunning();
      System.out.println("WireMock stopping: running = " + running);
      return !running;
    });
  }
}
