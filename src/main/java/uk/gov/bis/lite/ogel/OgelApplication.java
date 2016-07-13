package uk.gov.bis.lite.ogel;

import com.fiestacabin.dropwizard.quartz.ManagedScheduler;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.exception.CheckLocalOgelExceptionMapper;
import uk.gov.bis.lite.ogel.exception.CustomJsonProcessingExceptionMapper;
import uk.gov.bis.lite.ogel.exception.OgelIDNotFoundException;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.exception.SOAPParseException;
import uk.gov.bis.lite.ogel.healthcheck.SpireHealthCheck;
import uk.gov.bis.lite.ogel.resource.ApplicableOgelResource;
import uk.gov.bis.lite.ogel.resource.OgelResource;
import uk.gov.bis.lite.ogel.resource.auth.SimpleAuthenticator;

public class OgelApplication extends Application<MainApplicationConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(OgelApplication.class);
  private GuiceBundle<MainApplicationConfiguration> guiceBundle;

  public static void main(String[] args) throws Exception {
    new OgelApplication().run(args);
  }

  @Override
  public void run(MainApplicationConfiguration configuration, Environment environment) {
    final Injector injector = guiceBundle.getInjector();

    environment.jersey().register(OgelNotFoundException.OgelNotFoundExceptionHandler.class);
    environment.jersey().register(SOAPParseException.SOAPParseExceptionHandler.class);
    //Authorization and authentication handlers
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
        .setAuthenticator(new SimpleAuthenticator(configuration.getLogin(), configuration.getPassword()))
        .setRealm("OGEL Service Admin Authentication")
        .buildAuthFilter()));
    environment.jersey().register(OgelIDNotFoundException.OgelIDNotFoundExceptionHandler.class);
    environment.jersey().register(CacheNotPopulatedException.CacheNotPopulatedExceptionHandler.class);
    environment.jersey().register(CustomJsonProcessingExceptionMapper.class);
    environment.jersey().register(CheckLocalOgelExceptionMapper.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class));

    try {
      ManagedScheduler managedScheduler = injector.getInstance(ManagedScheduler.class);
      managedScheduler.start();
    } catch (Exception e) {
      LOGGER.error("An error occurred wiring the guice managed quartz scheduler", e);
    }
  }

  @Override
  public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
    guiceBundle = GuiceBundle.<MainApplicationConfiguration>builder()
        .modules(new GuiceModule())
        .installers(ResourceInstaller.class, HealthCheckInstaller.class)
        .extensions(OgelResource.class, ApplicableOgelResource.class, SpireHealthCheck.class)
        .build(Stage.PRODUCTION);

    bootstrap.addBundle(guiceBundle);
  }
}
