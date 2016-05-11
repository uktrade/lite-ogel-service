package uk.gov.bis.lite.ogel.config.cache;

import com.fiestacabin.dropwizard.quartz.Scheduled;
import com.google.inject.Inject;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.Main;

import java.util.concurrent.TimeUnit;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Scheduled(interval = 30, unit = TimeUnit.SECONDS)
public class RefreshCacheJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshCacheJob.class);

  @Inject
  CacheManager cacheManager;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    final Ehcache ogelCache = cacheManager.getEhcache(Main.CACHE_NAME);

    if (ogelCache instanceof SelfPopulatingCache) {
      SelfPopulatingCache cache = (SelfPopulatingCache) ogelCache;
      try {
        LOGGER.info("Cache Refreshing Job is Executing.");
        cache.refresh("ogelList");
      } catch (CacheException e) {
        LOGGER.error("An error occurred while trying to call EhCache Refresh ", e);
      }
    }
  }
}
