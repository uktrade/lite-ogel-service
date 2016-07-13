package uk.gov.bis.lite.ogel.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

public class SpireHealthCheck extends NamedHealthCheck {
  private final Logger LOGGER = LoggerFactory.getLogger(SpireHealthCheck.class);

  @Override
  protected Result check() throws Exception {
    SpireHealthStatus healthStatus = SpireOgelService.getHealthStatus();
    if (healthStatus.isHealthy()) {
      LOGGER.info("Communication with Spire is Healthy. Ogel list last updated at {}", healthStatus.getLastUpdated());
      return Result.healthy();
    } else {
      LOGGER.warn("Failed communication with Spire");
      return Result.unhealthy(healthStatus.getErrorMessage());
    }
  }

  @Override
  public String getName() {
    return "SpireHealthCheck";
  }
}
