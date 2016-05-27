package uk.gov.bis.lite.ogel;

import com.fiestacabin.dropwizard.quartz.GuiceJobFactory;
import com.fiestacabin.dropwizard.quartz.ManagedScheduler;
import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.google.inject.Injector;
import com.google.inject.Stage;
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
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.cache.CacheConfig;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundExceptionHandler;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundExceptionHandler;
import uk.gov.bis.lite.ogel.resource.SpireMergedOgelViewResource;
import uk.gov.bis.lite.ogel.resource.SpireOgelResource;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

public class Main extends Application<MainApplicationConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  public static final String CACHE_NAME = "ogelCache";
  public static final String CACHE_KEY = "ogelList";
  private GuiceBundle<MainApplicationConfiguration> guiceBundle;
  private SchedulerFactory sf = new StdSchedulerFactory();

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void run(MainApplicationConfiguration configuration, Environment environment) {
    final Injector injector = guiceBundle.getInjector();
    final CacheManager cacheManager = CacheManager.getInstance();
    final SpireOgelService ogelService = injector.getInstance(SpireOgelService.class);

    environment.jersey().register(SpireOgelResource.class);
    environment.jersey().register(SpireMergedOgelViewResource.class);
    environment.jersey().register(OgelNotFoundExceptionHandler.class);
    environment.jersey().register(LocalOgelNotFoundExceptionHandler.class);

    Cache customCache = cacheManager.getCache(CACHE_NAME);
    SelfPopulatingCache selfPopulatingCache = new CacheConfig().createSelfPopulatingCacheFromEhCache(customCache, ogelService);

    cacheManager.replaceCacheWithDecoratedCache(customCache, selfPopulatingCache);

    final GuiceJobFactory guiceJobFactory = injector.getInstance(GuiceJobFactory.class);
    final SchedulerConfiguration schedulerConfiguration = injector.getInstance(SchedulerConfiguration.class);
    try {
      ManagedScheduler managedScheduler = new ManagedScheduler(sf.getScheduler(), guiceJobFactory, schedulerConfiguration);
      managedScheduler.start();
    } catch (Exception e) {
      LOGGER.error("An error occurred wiring the guice managed quartz scheduler", e);
    }
  }

  @Override
  public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
    guiceBundle = GuiceBundle.<MainApplicationConfiguration>newBuilder()
        .addModule(new GuiceModule())
        .setConfigClass(MainApplicationConfiguration.class)
        .build(Stage.PRODUCTION);

    bootstrap.addBundle(guiceBundle);
  }
}
