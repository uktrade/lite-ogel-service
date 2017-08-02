package uk.gov.bis.lite.ogel.healthcheck;

import com.google.inject.Inject;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;

public class SpireOgelReadinessService implements ReadinessService {

  private final SpireOgelCache spireOgelCache;

  @Inject
  public SpireOgelReadinessService(SpireOgelCache spireOgelCache) {
    this.spireOgelCache = spireOgelCache;
  }

  @Override
  public boolean isReady() {
    return spireOgelCache.isSpireReady();
  }
}
