package uk.gov.bis.lite.ogel;

import com.fiestacabin.dropwizard.quartz.GuiceJobFactory;
import com.fiestacabin.dropwizard.quartz.ManagedScheduler;
import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.database.exception.SOAPParseExceptionHandler;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.resource.OgelResource;
import uk.gov.bis.lite.ogel.resource.SpireOgelResource;
import uk.gov.bis.lite.ogel.resource.auth.SimpleAuthenticator;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application<MainApplicationConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private GuiceBundle<MainApplicationConfiguration> guiceBundle;
  private SchedulerFactory sf = new StdSchedulerFactory();
  public static Map<String, SpireOgel> cache = new HashMap<>();

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

  @Override
  public void run(MainApplicationConfiguration configuration, Environment environment) {
    final Injector injector = guiceBundle.getInjector();

    environment.jersey().register(OgelNotFoundException.OgelNotFoundExceptionHandler.class);
    environment.jersey().register(SOAPParseExceptionHandler.class);
    //Authorization and authentication handlers
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
            .setAuthenticator(new SimpleAuthenticator(configuration.getLogin(), configuration.getPassword()))
            .setRealm("OGEL Service Admin Authentication")
            .buildAuthFilter()));
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class));

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
    guiceBundle = GuiceBundle.<MainApplicationConfiguration>builder()
        .modules(new GuiceModule())
        .installers(ResourceInstaller.class)
        .extensions(OgelResource.class, SpireOgelResource.class)
        .build(Stage.PRODUCTION);

    bootstrap.addBundle(guiceBundle);
  }
}
