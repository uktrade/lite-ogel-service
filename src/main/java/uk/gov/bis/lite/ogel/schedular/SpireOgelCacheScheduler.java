package uk.gov.bis.lite.ogel.schedular;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceImpl;

public class SpireOgelCacheScheduler implements Managed {

  private final org.quartz.Scheduler scheduler;
  private final MainApplicationConfiguration config;
  private final SpireOgelServiceImpl spireOgelService;

  @Inject
  public SpireOgelCacheScheduler(Scheduler scheduler, MainApplicationConfiguration config, SpireOgelServiceImpl spireOgelService) {
    this.scheduler = scheduler;
    this.config = config;
    this.spireOgelService = spireOgelService;
  }

  @Override
  public void start() throws Exception {
    JobKey key = JobKey.jobKey("spireOgelCacheJobCron");
    JobDetail jobDetail = newJob(SpireOgelCacheJob.class)
        .withIdentity(key)
        .build();

    CronTrigger trigger = newTrigger()
        .withIdentity(TriggerKey.triggerKey("spireOgelCacheJobCron"))
        .withSchedule(cronSchedule(config.getSpireOgelCacheJobCron()))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
    scheduler.triggerJob(key);
    scheduler.startDelayed(3); // to avoid premature process on startup
  }

  @Override
  public void stop() throws Exception {
    scheduler.shutdown(true);
  }
}
