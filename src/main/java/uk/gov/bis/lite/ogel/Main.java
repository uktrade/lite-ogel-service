package uk.gov.bis.lite.ogel;

import com.fiestacabin.dropwizard.quartz.GuiceJobFactory;
import com.fiestacabin.dropwizard.quartz.ManagedScheduler;
import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.cache.CacheConfig;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.database.dao.SqliteLocalOgelDAOImpl;
import uk.gov.bis.lite.ogel.database.utility.LocalOgelDBUtil;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.resource.SpireMergedOgelViewResource;
import uk.gov.bis.lite.ogel.resource.SpireOgelResource;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.util.List;

public class Main extends Application<MainApplicationConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  public static final String CACHE_NAME = "ogelCache";
  public static final String CACHE_KEY = "ogelList";
  private GuiceBundle<MainApplicationConfiguration> guiceBundle;
  //TODO find a way to inject this via Guice
  public static DBI jdbi;

  private SchedulerFactory sf = new StdSchedulerFactory();

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void run(MainApplicationConfiguration configuration, Environment environment) {
    final Injector injector = guiceBundle.getInjector();
    final DBIFactory factory = new DBIFactory();
    jdbi = factory.build(environment, configuration.getDataSourceFactory(), "sqlite");

    final CacheManager cacheManager = CacheManager.getInstance();
    final SpireOgelService ogelService = injector.getInstance(SpireOgelService.class);
    final Handle handle = jdbi.open();
    final LocalOgelDAO localOgelDAO = handle.attach(SqliteLocalOgelDAOImpl.class);
    try {
      populateLocalOgelDatabase(localOgelDAO);
    } catch (IOException e) {
      LOGGER.error("An error occurred trying to initialize the database", e);
    }

    environment.jersey().register(SpireOgelResource.class);
    environment.jersey().register(SpireMergedOgelViewResource.class);

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

    /*bootstrap.addBundle(new FlywayBundle<MainApplicationConfiguration>() {
      @Override
      public DataSourceFactory getDataSourceFactory(MainApplicationConfiguration configuration) {
        return configuration.getDataSourceFactory();
      }

      @Override
      public FlywayFactory getFlywayFactory(MainApplicationConfiguration configuration) {
        return new FlywayFactory(); //should create a default flyway factory
      }
    });*/
  }

  private void populateLocalOgelDatabase(LocalOgelDAO localOgelDAO) throws IOException {
    localOgelDAO.createLocalOgelTable();
    localOgelDAO.createConditionListTable();
    final List<LocalOgel> localOgels = LocalOgelDBUtil.retrieveAllOgelsFromJSON();
    localOgels.stream().forEach(localOgelDAO::insertLocalOgel);
  }
}
