package uk.gov.bis.lite.ogel.config.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.config.quartz.QuartzConfig;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.Collections;
import java.util.List;

public class CacheConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);

  public SelfPopulatingCache createSelfPopulatingCacheFromEhCache(Cache customCache, SpireOgelService ogelService, Scheduler scheduler) {
    SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(customCache, key -> {
      if ("ogelList".equals(key)) {
        try {
          List<SpireOgel> spireOgelList = ogelService.getAllOgels();
          //back to normal frequency
          LOGGER.info("Switching back to normal frequency {} after a successful execution of the cache refreshing Job",
              QuartzConfig.normalFreqTrigger.getCronExpression());
          scheduler.rescheduleJob(QuartzConfig.fasterFreqTrigger.getKey(), QuartzConfig.normalFreqTrigger);
          return spireOgelList;
        } catch (Exception e) {
          LOGGER.error("An error occurred while trying to refresh the cache elements", e);
          LOGGER.info("Switching to aggressive job frequency {} after a successful execution of the cache refreshing Job",
              QuartzConfig.fasterFreqTrigger.getCronExpression());
          scheduler.rescheduleJob(QuartzConfig.normalFreqTrigger.getKey(), QuartzConfig.fasterFreqTrigger);
        }
      }
      return Collections.emptyList();
    });
    return selfPopulatingCache;
  }
}
