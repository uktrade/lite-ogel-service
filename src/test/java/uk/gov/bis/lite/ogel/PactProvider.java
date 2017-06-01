package uk.gov.bis.lite.ogel;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.service.LocalOgelServiceMock;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceMock;

@RunWith(PactRunner.class)
@Provider("lite-ogel-service")
@PactBroker(host = "pact-broker.mgmt.licensing.service.trade.gov.uk.test", port = "80")
public class PactProvider {

  @ClassRule
  public static final DropwizardAppRule<MainApplicationConfiguration> RULE =
      new DropwizardAppRule<>(TestOgelApplication.class, resourceFilePath("service-test.yaml"));

  @TestTarget // Annotation denotes Target that will be used for tests
  public final Target target = new HttpTarget(RULE.getLocalPort()); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)

  @State("provided OGEL exists")
  public void existingOgelState() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(SpireOgelServiceMock.class).setMissingOgel(false);
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(LocalOgelServiceMock.class).setMissingLocalOgel(false);
  }

  @State("provided OGEL does not exist")
  public void missingOgelState() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(SpireOgelServiceMock.class).setMissingOgel(true);
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(LocalOgelServiceMock.class).setMissingLocalOgel(true);
  }

}
