package com.bis.lite.ogel.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

import io.dropwizard.Configuration;

public class MainApplicationConfiguration extends Configuration{

    @NotEmpty
    @JsonProperty
    private String soapUrl;

    @NotEmpty
    @JsonProperty
    private String cronCacheRefreshJobInterval;

    @NotEmpty
    @JsonProperty
    private String cronFastCacheRefreshJobInterval;

    @NotEmpty
    @JsonProperty
    private String soapUserName;

    @NotEmpty
    @JsonProperty
    private String soapPassword;

    @NotEmpty
    @JsonProperty
    private String cacheTimeout;


    public String getSoapUrl() {
        return soapUrl;
    }

    public void setSoapUrl(String soapUrl) {
        this.soapUrl = soapUrl;
    }

    public String getSoapUserName() {
        return soapUserName;
    }

    public String getSoapPassword() {
        return soapPassword;
    }

    public String getCacheTimeout() {
        return cacheTimeout;
    }

    public String getCronCacheRefreshJobInterval() {
        return cronCacheRefreshJobInterval;
    }

    public String getCronFastCacheRefreshJobInterval() {
        return cronFastCacheRefreshJobInterval;
    }
}
