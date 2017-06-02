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
import uk.gov.bis.lite.ogel.service.ApplicableOgelServiceMock;
import uk.gov.bis.lite.ogel.service.ControlCodeConditionsServiceMock;
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

  private void resetMockState(){
    getLocalOgelServiceMock()
        .setMissingLocalOgel(false);
    getSpireOgelServiceMock()
        .setMissingOgel(false)
        .setVirtualEu(false);
    getApplicableOgelServiceMock()
        .setOgelFound(true)
        .setValidActivityType(true);
    getControlCodeConditionsServiceMock()
        .setConditionsFound(true)
        .setControlCodeDescriptionsFound(true)
        .setControlCodeDescriptionsMissingControlCodes(false);
  }

  private LocalOgelServiceMock getLocalOgelServiceMock() {
    return InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(LocalOgelServiceMock.class);
  }

  private SpireOgelServiceMock getSpireOgelServiceMock() {
    return InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(SpireOgelServiceMock.class);
  }

  private ApplicableOgelServiceMock getApplicableOgelServiceMock() {
    return InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(ApplicableOgelServiceMock.class);
  }

  private ControlCodeConditionsServiceMock getControlCodeConditionsServiceMock() {
    return InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(ControlCodeConditionsServiceMock.class);
  }

  @State("provided OGEL exists")
  public void existingOgelState() {
    resetMockState();
  }

  @State("provided OGEL does not exist")
  public void missingOgelState() {
    resetMockState();
    getLocalOgelServiceMock().setMissingLocalOgel(true);
    getSpireOgelServiceMock().setMissingOgel(true);
  }

  @State("applicable ogels exist for given parameters")
  public void applicableOgelsExist() {
    resetMockState();
  }

  @State("applicable ogels exist for multiple activity types")
  public void applicableOgelsExistMultipleActivities() {
    resetMockState();
  }

  @State("no applicable ogels exist for given parameters")
  public void applicableOgelsDoNotExist() {
    resetMockState();
    getApplicableOgelServiceMock().setOgelFound(false);
  }

  @State("activity type does not exist")
  public void applicableOgelActivityTypeDoesNotExist() {
    resetMockState();
    getApplicableOgelServiceMock().setValidActivityType(false);
  }

  @State("conditions exist with related control codes for given ogel and control code")
  public void conditionsExistWithRelatedControlsForOgelAndControlCode() {
    resetMockState();
  }

  @State("conditions exist for given ogel and control code")
  public void conditionsExistForOgelAndControlCode() {
    resetMockState();
    getControlCodeConditionsServiceMock().setControlCodeDescriptionsFound(false);
  }


  @State("conditions exist with missing related control codes for given ogel and control code")
  public void conditionsExistWithMissingControlCodeForOgelAndControlCode() {
    resetMockState();
    getControlCodeConditionsServiceMock().setControlCodeDescriptionsMissingControlCodes(true);
  }

  @State("no conditions exist for given ogel and control code")
  public void conditionsDoNotExistForOgelAndControlCode() {
    resetMockState();
    getControlCodeConditionsServiceMock().setConditionsFound(false);
  }

  @State("parameters match virtual EU ogel")
  public void virtualEuOgelExists() {
    resetMockState();
    getSpireOgelServiceMock().setVirtualEu(true);
  }

  @State("parameters do not match virtual EU ogel")
  public void virtualEuOgelDoesNotExists() {
    resetMockState();
    getSpireOgelServiceMock().setVirtualEu(false);
  }
}
