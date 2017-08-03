package uk.gov.bis.lite.ogel.config.guice;

import static uk.gov.bis.lite.ogel.OgelApplication.SPIRE_OGEL_CACHE;

import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import uk.gov.bis.lite.common.jersey.filter.ClientCorrelationIdFilter;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;
import uk.gov.bis.lite.common.spire.client.SpireClientConfig;
import uk.gov.bis.lite.common.spire.client.SpireRequestConfig;
import uk.gov.bis.lite.ogel.cache.SpireOgelCache;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.database.dao.controlcodecondition.LocalControlCodeConditionDAO;
import uk.gov.bis.lite.ogel.database.dao.controlcodecondition.SqliteLocalControlCodeConditionDAOImpl;
import uk.gov.bis.lite.ogel.database.dao.ogel.LocalOgelDAO;
import uk.gov.bis.lite.ogel.database.dao.ogel.SqliteLocalOgelDAOImpl;
import uk.gov.bis.lite.ogel.healthcheck.SpireOgelReadinessService;
import uk.gov.bis.lite.ogel.service.ApplicableOgelService;
import uk.gov.bis.lite.ogel.service.ApplicableOgelServiceImpl;
import uk.gov.bis.lite.ogel.service.ControlCodeConditionsService;
import uk.gov.bis.lite.ogel.service.ControlCodeConditionsServiceImpl;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionServiceImpl;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.LocalOgelServiceImpl;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceImpl;
import uk.gov.bis.lite.ogel.spire.SpireOgelClient;
import uk.gov.bis.lite.ogel.spire.parsers.OgelTypeParser;

import javax.ws.rs.client.Client;

public class GuiceModule extends AbstractModule {

  @Provides
  @Singleton
  SpireOgelClient provideSpireOgelClient(MainApplicationConfiguration config) {
    return new SpireOgelClient(new OgelTypeParser(),
        new SpireClientConfig(config.getSpireClientUserName(), config.getSpireClientPassword(), config.getSpireClientUrl()),
        new SpireRequestConfig("SPIRE_OGEL_TYPES", "getOgelTypes", true));
  }

  @Provides
  @Named("cacheTimeout")
  public String provideCacheTimeoutInSeconds(MainApplicationConfiguration configuration) {
    return configuration.getCacheTimeout();
  }

  @Provides
  @Named("controlCodeServiceUrl")
  public String provideControlCodeServiceUrl(MainApplicationConfiguration configuration) {
    return configuration.getControlCodeServiceUrl();
  }

  @Provides
  @Named("virtualEuOgelId")
  public String provideVirtualEuOgelId(MainApplicationConfiguration configuration) {
    return configuration.getVirtualEuOgelId();
  }

  @Override
  protected void configure() {
    bind(SchedulerConfiguration.class).toInstance(new SchedulerConfiguration("uk.gov.bis.lite.ogel"));
    bind(LocalOgelDAO.class).to(SqliteLocalOgelDAOImpl.class);
    bind(LocalControlCodeConditionDAO.class).to(SqliteLocalControlCodeConditionDAOImpl.class);
    bind(LocalControlCodeConditionService.class).to(LocalControlCodeConditionServiceImpl.class);
    bind(LocalOgelService.class).to(LocalOgelServiceImpl.class);
    bind(SpireOgelService.class).to(SpireOgelServiceImpl.class);
    bind(ApplicableOgelService.class).to(ApplicableOgelServiceImpl.class);
    bind(ControlCodeConditionsService.class).to(ControlCodeConditionsServiceImpl.class);
    bind(ReadinessService.class).to(SpireOgelReadinessService.class);
  }

  @Provides
  @Singleton
  @Named("jdbi")
  public DBI provideDataSourceJdbi(Environment environment, MainApplicationConfiguration configuration) {
    final DBIFactory factory = new DBIFactory();
    return factory.build(environment, configuration.getDataSourceFactory(), "sqlite");
  }

  @Provides
  @Singleton
  Scheduler provideScheduler(SpireOgelCache spireOgelCache) throws SchedulerException {
    Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    scheduler.getContext().put(SPIRE_OGEL_CACHE, spireOgelCache);
    return scheduler;
  }

  @Provides
  @Singleton
  Client provideHttpClient(Environment environment, MainApplicationConfiguration configuration) {
    final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
        .build("jerseyClient");
    client.register(ClientCorrelationIdFilter.class);
    return client;
  }
}
