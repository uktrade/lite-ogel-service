package uk.gov.bis.lite.ogel.healthcheck;

import com.google.inject.Inject;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

public class SpireHealthCheck extends NamedHealthCheck {

  @Inject
  private SpireOgelService spireOgelService;

  @Override
  protected Result check() throws Exception {
    spireOgelService.getAllOgelsFromSpire();
    return Result.healthy();
  }

  @Override
  public String getName() {
    return "SpireHealthCheck";
  }
}
