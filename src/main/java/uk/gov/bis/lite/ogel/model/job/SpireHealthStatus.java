package uk.gov.bis.lite.ogel.model.job;

import java.time.LocalDateTime;

public class SpireHealthStatus {

  private boolean isHealthy;
  private LocalDateTime lastUpdated;
  private String errorMessage;

  public boolean isHealthy() {
    return isHealthy;
  }

  public void setHealthy(boolean healthy) {
    this.isHealthy = healthy;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
