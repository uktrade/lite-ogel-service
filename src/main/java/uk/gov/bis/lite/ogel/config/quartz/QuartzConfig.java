package uk.gov.bis.lite.ogel.config.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;

public class QuartzConfig {

  public static CronTrigger normalFreqTrigger, fasterFreqTrigger;

  public static void initializeJobScheduler(MainApplicationConfiguration configuration) throws SchedulerException {
    fasterFreqTrigger = newTrigger()
        .withIdentity("aggresiveTrigger")
        .withSchedule(cronSchedule(configuration.getCronFastCacheRefreshJobInterval()))
        .build();

    normalFreqTrigger = newTrigger()
        .withIdentity("dailyTrigger")
        .withSchedule(cronSchedule(configuration.getCronCacheRefreshJobInterval()))
        .build();
  }
}
