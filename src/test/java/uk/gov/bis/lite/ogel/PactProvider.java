package uk.gov.bis.lite.ogel;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceMock;

@RunWith(PactRunner.class)
@Provider("lite-ogel-service")
@PactFolder("C:\\work\\frontend\\lite-ogel-registration\\target\\pacts")
public class PactProvider {

  @ClassRule
  public static final DropwizardAppRule<MainApplicationConfiguration> RULE =
      new DropwizardAppRule<>(TestOgelApplication.class, resourceFilePath("service-test.yaml"));

  @TestTarget // Annotation denotes Target that will be used for tests
  public final Target target = new HttpTarget(RULE.getLocalPort()); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)

  @State("existing ogel")
  public void toExistingAddressState() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(SpireOgelServiceMock.class).setUpExistingOgel();
  }

  @State("missing ogel")
  public void toInvalidAddressState() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(SpireOgelServiceMock.class).setUpMissingOgel();
  }

}
