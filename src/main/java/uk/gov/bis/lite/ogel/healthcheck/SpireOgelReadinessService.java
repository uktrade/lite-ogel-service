package uk.gov.bis.lite.ogel.healthcheck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;

@Singleton
public class SpireOgelReadinessService implements ReadinessService {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public class SpireOgelReadiness {
    public final boolean ready;
    public final String message;

    public SpireOgelReadiness(boolean ready, String message) {
      this.ready = ready;
      this.message = message;
    }
  }

  private final SpireOgelCache spireOgelCache;

  @Inject
  public SpireOgelReadinessService(SpireOgelCache spireOgelCache) {
    this.spireOgelCache = spireOgelCache;
  }

  @Override
  public boolean isReady() {
    return spireOgelCache.isSpireReady();
  }

  @Override
  public JsonNode readinessJson() {
    SpireOgelReadiness spireOgelReadiness;
    if (isReady()) {
      spireOgelReadiness = new SpireOgelReadiness(true, null);
    } else {
      spireOgelReadiness = new SpireOgelReadiness(false, "Spire ogel cache is not populated.");
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.valueToTree(spireOgelReadiness);
  }
}
