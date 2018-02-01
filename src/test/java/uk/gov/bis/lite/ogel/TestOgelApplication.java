package uk.gov.bis.lite.ogel;

import com.google.inject.util.Modules;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;

public class TestOgelApplication extends OgelApplication {

  public TestOgelApplication() {
    super(Modules.override(new GuiceModule()).with(new GuiceTestModule()));
  }

  protected void flywayMigrate(MainApplicationConfiguration configuration) {
    // do nothing
  }

}
