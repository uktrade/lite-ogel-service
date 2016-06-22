package uk.gov.bis.lite.ogel.config.guice;

import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.database.dao.SqliteLocalOgelDAOImpl;

public class GuiceModule extends AbstractModule {

  @Provides
  @Named("soapUrl")
  public String provideSpireOgelUrl(MainApplicationConfiguration configuration) {
    return configuration.getSoapUrl();
  }

  @Provides
  @Named("soapUserName")
  public String provideSpireOgelClientUserName(MainApplicationConfiguration configuration) {
    return configuration.getSoapUserName();
  }

  @Provides
  @Named("soapPassword")
  public String provideSpireOgelClientPassword(MainApplicationConfiguration configuration) {
    return configuration.getSoapPassword();
  }

  @Provides
  @Named("cacheTimeout")
  public String provideCacheTimeoutInSeconds(MainApplicationConfiguration configuration) {
    return configuration.getCacheTimeout();
  }

  @Override
  protected void configure() {
    bind(SchedulerConfiguration.class).toInstance(new SchedulerConfiguration("uk.gov.bis.lite.ogel"));
    bind(LocalOgelDAO.class).to(SqliteLocalOgelDAOImpl.class);
  }

  @Provides
  @Named("jdbi")
  public DBI provideDataSourceJdbi(Environment environment, MainApplicationConfiguration configuration) {
    final DBIFactory factory = new DBIFactory();
    return factory.build(environment, configuration.getDataSourceFactory(), "sqlite");
  }

  @Provides
  @Singleton
  Scheduler provideScheduler() throws SchedulerException {
    Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
    return defaultScheduler;
  }
}
