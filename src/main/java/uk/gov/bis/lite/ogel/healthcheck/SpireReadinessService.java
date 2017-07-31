package uk.gov.bis.lite.ogel.healthcheck;

import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;

public class SpireReadinessService implements ReadinessService {

  @Override
  public boolean isReady() {
    return SpireOgelCache.isSpireReady();
  }
}
