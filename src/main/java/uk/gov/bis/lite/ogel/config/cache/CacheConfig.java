package uk.gov.bis.lite.ogel.config.cache;

import static uk.gov.bis.lite.ogel.Main.CACHE_KEY;

import net.sf.ehcache.Cache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.Collections;
import java.util.List;

public class CacheConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);

  public SelfPopulatingCache createSelfPopulatingCacheFromEhCache(Cache customCache, SpireOgelService ogelService, Scheduler scheduler) {
    SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(customCache, key -> {
      if (CACHE_KEY.equals(key)) {
        try {
          List<SpireOgel> spireOgelList = ogelService.initializeCache();
          LOGGER.info("Spire Ogel Cache has been successfully updated.");
          return spireOgelList;
        } catch (Exception e) {
          LOGGER.error("An error occurred while trying to refresh the cache elements", e);
        }
      }
      return Collections.emptyList();
    });
    return selfPopulatingCache;
  }
}
