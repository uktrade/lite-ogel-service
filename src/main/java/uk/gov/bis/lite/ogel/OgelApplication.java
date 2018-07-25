package uk.gov.bis.lite.ogel;

import com.codahale.metrics.servlets.AdminServlet;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.common.auth.admin.AdminConstraintSecurityHandler;
import uk.gov.bis.lite.common.auth.basic.SimpleAuthenticator;
import uk.gov.bis.lite.common.auth.basic.SimpleAuthorizer;
import uk.gov.bis.lite.common.auth.basic.User;
import uk.gov.bis.lite.common.jersey.filter.ContainerCorrelationIdFilter;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessServlet;
import uk.gov.bis.lite.common.paas.db.CloudFoundryEnvironmentSubstitutor;
import uk.gov.bis.lite.ogel.config.MainApplicationConfiguration;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.exception.CheckLocalOgelExceptionMapper;
import uk.gov.bis.lite.ogel.healthcheck.SpireHealthCheck;
import uk.gov.bis.lite.ogel.resource.ValidateResource;
import uk.gov.bis.lite.ogel.resource.ApplicableOgelResource;
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
  public void run(MainApplicationConfiguration config, Environment environment) {

    // Authorization and authentication handlers
    SimpleAuthenticator simpleAuthenticator = new SimpleAuthenticator(config.getAdminLogin(),
        config.getAdminPassword(),
        config.getServiceLogin(),
        config.getServicePassword());
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
        .setAuthenticator(simpleAuthenticator)
        .setAuthorizer(new SimpleAuthorizer())
        .setRealm("OGEL Service Authentication")
        .buildAuthFilter()));

    Injector injector = guiceBundle.getInjector();
    ReadinessServlet readinessServlet = injector.getInstance(ReadinessServlet.class);
    environment.admin().addServlet("ready", readinessServlet).addMapping("/ready");

    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

    environment.jersey().register(CacheNotPopulatedException.CacheNotPopulatedExceptionHandler.class);
    environment.jersey().register(CheckLocalOgelExceptionMapper.class);
    environment.jersey().register(ContainerCorrelationIdFilter.class);

    environment.admin().addServlet("admin", new AdminServlet()).addMapping("/admin");
    environment.admin().setSecurityHandler(new AdminConstraintSecurityHandler(config.getServiceLogin(), config.getServicePassword()));


    flywayMigrate(config);
  }

  protected void flywayMigrate(MainApplicationConfiguration configuration) {
    // Perform/validate flyway migration on startup
    DataSourceFactory dataSourceFactory = configuration.getDatabase();
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
    flyway.migrate();
  }

  @Override
  public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
    // Load config from a resource (i.e. file within the JAR), and substitute environment variables into it
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
        new ResourceConfigurationSourceProvider(), new CloudFoundryEnvironmentSubstitutor()));

    guiceBundle = GuiceBundle.<MainApplicationConfiguration>builder()
        .modules(module)
        .installers(ResourceInstaller.class, HealthCheckInstaller.class, ManagedInstaller.class)
        .extensions(ValidateResource.class, ApplicableOgelResource.class, OgelResource.class,
            SpireHealthCheck.class, VirtualEuResource.class, SpireOgelCacheScheduler.class)
        .build(Stage.PRODUCTION);

    bootstrap.addBundle(guiceBundle);
  }
}

