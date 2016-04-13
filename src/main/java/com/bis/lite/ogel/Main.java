package com.bis.lite.ogel;

import com.bis.lite.ogel.config.MainApplicationConfiguration;
import com.bis.lite.ogel.config.guice.SpireOgelModule;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Main extends Application<MainApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void run(MainApplicationConfiguration configuration, Environment environment) throws Exception {

    }

    @Override
    public void initialize(Bootstrap<MainApplicationConfiguration> bootstrap) {
        GuiceBundle<MainApplicationConfiguration> guiceBundle = GuiceBundle.<MainApplicationConfiguration>newBuilder()
                .addModule(new SpireOgelModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(MainApplicationConfiguration.class)
                .build();

        bootstrap.addBundle(guiceBundle);
    }
}
