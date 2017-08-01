package uk.gov.bis.lite.ogel.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;

public class SpireLivenessCheck extends NamedHealthCheck {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpireLivenessCheck.class);

  @Override
  protected Result check() throws Exception {
    if(!SpireOgelCache.isSpireReady()) {

      return Result.healthy("Loading SpireOgelCache...");
    }
    return null;
  }

  @Override
  public String getName() {
    return "SpireLivenessCheck";
  }
}
