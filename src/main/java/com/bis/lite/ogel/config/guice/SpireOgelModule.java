package com.bis.lite.ogel.config.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import com.bis.lite.ogel.config.MainApplicationConfiguration;

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

    @Provides
    @Named("cronCacheRefreshJobInterval")
    public String provideCacheRefreshInterval(MainApplicationConfiguration configuration){
        return configuration.getCronCacheRefreshJobInterval();
    }

    @Provides
    @Named("cronCacheRefreshJobInterval")
    public String provideFastCacheRefreshInterval(MainApplicationConfiguration configuration){
        return configuration.getCronFastCacheRefreshJobInterval();
    }

    @Override
    protected void configure() {
        System.out.println("Inside Guice Module Config");
    }
}
