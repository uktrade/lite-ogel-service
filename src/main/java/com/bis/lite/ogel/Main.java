package com.bis.lite.ogel;

import com.bis.lite.ogel.config.MainApplicationConfiguration;
import com.bis.lite.ogel.config.cache.CacheRefreshmentJob;
import com.bis.lite.ogel.config.guice.SpireOgelModule;
import com.bis.lite.ogel.model.SpireOgel;
import com.bis.lite.ogel.service.SpireOgelService;
import com.hubspot.dropwizard.guice.GuiceBundle;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main extends Application<MainApplicationConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private GuiceBundle<MainApplicationConfiguration> guiceBundle;
    private JobDetail job;
    private CronTrigger normalFreqTrigger, fasterFreqTrigger;
    SchedulerFactory sf = new StdSchedulerFactory();

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void run(MainApplicationConfiguration configuration, Environment environment) throws Exception {
        final CacheManager cacheManager = guiceBundle.getInjector().getInstance(CacheManager.class);
        final SpireOgelService ogelService = guiceBundle.getInjector().getInstance(SpireOgelService.class);

        initializeJobScheduler(configuration);
        Cache customCache = cacheManager.getCache("ogelCache");

        SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(customCache, key -> {
            if ("ogelList".equals(key)) {
                try {
                    List<SpireOgel> spireOgelList = ogelService.getAllOgels();
                    Scheduler scheduler = sf.getScheduler();
                    //back to normal frequency
                    LOGGER.info("Switching back to normal frequency {} after a successful execution of the cache refreshing Job",
                            normalFreqTrigger.getCronExpression());
                    scheduler.rescheduleJob(fasterFreqTrigger.getKey(), normalFreqTrigger);
                    return spireOgelList;
                } catch (Exception e) {
                    LOGGER.error("An error occurred while trying to refresh the cache elements", e);
                    Scheduler scheduler = sf.getScheduler();
                    LOGGER.info("Switching to aggressive job frequency {} after a successful execution of the cache refreshing Job",
                            fasterFreqTrigger.getCronExpression());
                    scheduler.rescheduleJob(normalFreqTrigger.getKey(), fasterFreqTrigger);
                }
            }
            return Collections.emptyList();
        });
        cacheManager.replaceCacheWithDecoratedCache(customCache, selfPopulatingCache);
        selfPopulatingCache.get("ogelList");
    }

    @Override
    public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
        guiceBundle = GuiceBundle.<MainApplicationConfiguration>newBuilder()
                .addModule(new SpireOgelModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(MainApplicationConfiguration.class)
                .build();

        bootstrap.addBundle(guiceBundle);
        LOGGER.info("Guice bundle with auto component scan feature successfully added to Dropwizard");
    }

    private void initializeJobScheduler(MainApplicationConfiguration configuration) throws SchedulerException {
        job = newJob(CacheRefreshmentJob.class)
                .withIdentity("cacheRefreshJob")
                .build();

        fasterFreqTrigger = newTrigger()
                .withIdentity("aggresiveTrigger")
                .withSchedule(cronSchedule(configuration.getCronFastCacheRefreshJobInterval()))
                .build();

        normalFreqTrigger = newTrigger()
                .withIdentity("dailyTrigger")
                .withSchedule(cronSchedule(configuration.getCronCacheRefreshJobInterval())) //0 0 23 * * ?
                .build();
        Scheduler scheduler = sf.getScheduler();
        scheduler.scheduleJob(job, normalFreqTrigger);
        scheduler.start();
    }
}
