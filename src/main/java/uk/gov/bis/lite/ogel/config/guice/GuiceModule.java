package uk.gov.bis.lite.ogel.config.guice;

import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.ehcache.CacheManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

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
    System.out.println("Inside Guice Module Config");
    bind(SchedulerConfiguration.class).toInstance(new SchedulerConfiguration("uk.gov.bis.lite.ogel"));
    bind(CacheManager.class).toInstance(CacheManager.create());
  }

  @Provides
  @Singleton
  Scheduler provideScheduler() throws SchedulerException {
    return StdSchedulerFactory.getDefaultScheduler();
  }
}
