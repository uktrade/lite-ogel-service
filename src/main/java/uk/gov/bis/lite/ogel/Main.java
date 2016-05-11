package uk.gov.bis.lite.ogel;

import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.cache.CacheConfig;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.config.quartz.QuartzConfig;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import com.fiestacabin.dropwizard.quartz.GuiceJobFactory;
import com.fiestacabin.dropwizard.quartz.ManagedScheduler;
import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application<MainApplicationConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  public static final String CACHE_NAME = "ogelCache";
  private GuiceBundle<MainApplicationConfiguration> guiceBundle;

  SchedulerFactory sf = new StdSchedulerFactory();

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void run(MainApplicationConfiguration configuration, Environment environment) throws Exception {
    final CacheManager cacheManager = CacheManager.getInstance();
    final SpireOgelService ogelService = guiceBundle.getInjector().createChildInjector().getInstance(SpireOgelService.class);

    QuartzConfig.initializeJobScheduler(configuration);
    Cache customCache = cacheManager.getCache(CACHE_NAME);

    SelfPopulatingCache selfPopulatingCache = new CacheConfig().createSelfPopulatingCacheFromEhCache(customCache, ogelService, sf.getScheduler());
    cacheManager.replaceCacheWithDecoratedCache(customCache, selfPopulatingCache);
    selfPopulatingCache.get(CACHE_NAME);
  }

  @Override
  public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
    guiceBundle = GuiceBundle.<MainApplicationConfiguration>newBuilder()
        .addModule(new GuiceModule())
        .enableAutoConfig(getClass().getPackage().getName())
        .setConfigClass(MainApplicationConfiguration.class)
        .build();

    bootstrap.addBundle(guiceBundle);
    final GuiceJobFactory guiceJobFactory = guiceBundle.getInjector().getInstance(GuiceJobFactory.class);
    final SchedulerConfiguration schedulerConfiguration = guiceBundle.getInjector().getInstance(SchedulerConfiguration.class);
    try {
      ManagedScheduler managedScheduler = new ManagedScheduler(sf.getScheduler(), guiceJobFactory, schedulerConfiguration);
      managedScheduler.start();
    } catch (Exception e) {
      LOGGER.error("An error occurred wiring the guice managed quartz scheduler", e);
    }
  }


}
