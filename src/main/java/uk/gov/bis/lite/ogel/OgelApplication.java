package uk.gov.bis.lite.ogel;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.common.jersey.filter.ContainerCorrelationIdFilter;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessServlet;
import uk.gov.bis.lite.ogel.auth.SimpleAuthenticator;
import uk.gov.bis.lite.ogel.auth.SimpleAuthorizer;
import uk.gov.bis.lite.ogel.auth.User;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.exception.CheckLocalOgelExceptionMapper;
import uk.gov.bis.lite.ogel.healthcheck.SpireHealthCheck;
import uk.gov.bis.lite.ogel.resource.AdminResource;
import uk.gov.bis.lite.ogel.resource.ApplicableOgelResource;
import uk.gov.bis.lite.ogel.resource.ControlCodeConditionsResource;
import uk.gov.bis.lite.ogel.resource.OgelResource;
import uk.gov.bis.lite.ogel.resource.VirtualEuResource;
import uk.gov.bis.lite.ogel.scheduler.SpireOgelCacheScheduler;

public class OgelApplication extends Application<MainApplicationConfiguration> {

  public static final String SPIRE_OGEL_CACHE = "spireOgelCache";
  private GuiceBundle<MainApplicationConfiguration> guiceBundle;
  private final Module module;

  public static void main(String[] args) throws Exception {
    new OgelApplication().run(args);
  }

  public OgelApplication() {
    this(new GuiceModule());
  }

  public OgelApplication(Module module) {
    super();
    this.module = module;
  }

  @Override
  public void run(MainApplicationConfiguration configuration, Environment environment) {
    final Injector injector = guiceBundle.getInjector();

    ReadinessServlet readinessServlet = injector.getInstance(ReadinessServlet.class);
    environment.admin().addServlet("ready", readinessServlet).addMapping("/ready");

    //Authorization and authentication handlers
    SimpleAuthenticator simpleAuthenticator = new SimpleAuthenticator(configuration.getAdminLogin(),
        configuration.getAdminPassword(),
        configuration.getServiceLogin(),
        configuration.getServicePassword());
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
        .setAuthenticator(simpleAuthenticator)
        .setAuthorizer(new SimpleAuthorizer())
        .setRealm("OGEL Service Authentication")
        .buildAuthFilter()));
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

    environment.jersey().register(CacheNotPopulatedException.CacheNotPopulatedExceptionHandler.class);
    environment.jersey().register(CheckLocalOgelExceptionMapper.class);
    environment.jersey().register(ContainerCorrelationIdFilter.class);

    //Perform/validate flyway migration on startup
    DataSourceFactory dataSourceFactory = configuration.getDatabase();
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
    flyway.migrate();
  }

  @Override
  public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
    guiceBundle = GuiceBundle.<MainApplicationConfiguration>builder()
        .modules(module)
        .installers(ResourceInstaller.class, HealthCheckInstaller.class, ManagedInstaller.class)
        .extensions(AdminResource.class, ApplicableOgelResource.class, OgelResource.class,
            ControlCodeConditionsResource.class, SpireHealthCheck.class, VirtualEuResource.class, SpireOgelCacheScheduler.class)
        .build(Stage.PRODUCTION);

    bootstrap.addBundle(guiceBundle);
  }
}
