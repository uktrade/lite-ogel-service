package uk.gov.bis.lite.ogel.scheduler;

import static uk.gov.bis.lite.ogel.OgelApplication.SPIRE_OGEL_CACHE;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;

public class SpireOgelCacheJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireOgelCacheJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOGGER.info("Start loading of spire ogels cache...");
    Scheduler scheduler = jobExecutionContext.getScheduler();

    try {
      SpireOgelCache spireOgelCache = (SpireOgelCache) scheduler.getContext().get(SPIRE_OGEL_CACHE);
      spireOgelCache.load();
    } catch (SchedulerException e) {
      LOGGER.error("Failed to load spire ogel cache.", e);
    }
  }
}
