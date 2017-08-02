package uk.gov.bis.lite.ogel.cache;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.common.spire.client.SpireRequest;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;
import uk.gov.bis.lite.ogel.spire.SpireOgelClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class SpireOgelCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireOgelCache.class);
  private volatile Map<String, SpireOgel> cache = new HashMap<>();
  private SpireHealthStatus healthStatus = SpireHealthStatus.unhealthy("Cache not initialised");
  private SpireOgelClient ogelClient;

  @Inject
  public SpireOgelCache(SpireOgelClient ogelClient) {
    this.ogelClient = ogelClient;
  }

  public void load() {
    try {
      List<SpireOgel> ogelList = getAllOgelsFromSpire();
      Map<String, SpireOgel> spireOgelCacheMap = ogelList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));

      if (spireOgelCacheMap.size() > 0) {
        cache = Collections.unmodifiableMap(spireOgelCacheMap);
        healthStatus = SpireHealthStatus.healthy();
        LOGGER.info("Cache has been successfully updated at {}", healthStatus.getLastUpdated());
      } else {
        healthStatus = SpireHealthStatus.unhealthy("SPIRE returned 0 OGELs");
        LOGGER.warn("Cache refresh job failed to retrieve new data from SPIRE.");
      }
    } catch (Throwable th) {
      LOGGER.warn("An unexpected error occurred getting OGEL data from SPIRE", th);
      healthStatus = SpireHealthStatus.unhealthy(th.getMessage());
    }
  }

  public Map<String, SpireOgel> getCache() {
    return cache;
  }

  public SpireHealthStatus getHealthStatus() {
    return healthStatus;
  }

  private List<SpireOgel> getAllOgelsFromSpire() {
    SpireRequest request = ogelClient.createRequest();
    return ogelClient.sendRequest(request);
  }
}