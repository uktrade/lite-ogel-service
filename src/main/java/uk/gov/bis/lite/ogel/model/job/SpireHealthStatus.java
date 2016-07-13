package uk.gov.bis.lite.ogel.model.job;

import java.time.LocalDateTime;

public class SpireHealthStatus {

  private final boolean healthy;
  private final LocalDateTime lastUpdated;
  private final String errorMessage;

  public static SpireHealthStatus healthy() {
    return new SpireHealthStatus(true, LocalDateTime.now(), null);
  }

  public static SpireHealthStatus unhealthy(String errorMessage) {
    return new SpireHealthStatus(false, LocalDateTime.now(), errorMessage);
  }

  private SpireHealthStatus(boolean healthy, LocalDateTime lastUpdated, String errorMessage) {
    this.healthy = healthy;
    this.lastUpdated = lastUpdated;
    this.errorMessage = errorMessage;
  }

  public boolean isHealthy() {
    return healthy;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
