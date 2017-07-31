package uk.gov.bis.lite.ogel.healthcheck;

import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;

public class SpireReadinessService implements ReadinessService {

  @Override
  public boolean isReady() {
    return true;
  }
}
