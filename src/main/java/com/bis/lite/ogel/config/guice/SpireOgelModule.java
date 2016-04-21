package com.bis.lite.ogel.config.guice;

import com.bis.lite.ogel.client.SpireOgelClient;
import com.bis.lite.ogel.config.MainApplicationConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class SpireOgelModule extends AbstractModule {

    @Provides
    @Named("soapUrl")
    public String provideSpireOgelUrl(MainApplicationConfiguration configuration) {
        return configuration.getSoapUrl();
    }

    @Provides
    @Named("soapUserName")
    public String provideSpireOgelClientUserName(MainApplicationConfiguration configuration){
        return configuration.getSoapUserName();
    }

    @Provides
    @Named("soapPassword")
    public String provideSpireOgelClientPassword(MainApplicationConfiguration configuration){
        return configuration.getSoapPassword();
    }

    @Provides
    @Named("cacheTimeout")
    public String provideCacheTimeoutInSeconds(MainApplicationConfiguration configuration){
        return configuration.getCacheTimeout();
    }

    @Override
    protected void configure() {
        System.out.println("Inside Guice Module Config");
    }
}
