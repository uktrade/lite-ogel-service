package com.bis.lite.ogel.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class MainApplicationConfiguration extends Configuration{

    @NotEmpty
    @JsonProperty
    private String soapUrl;


    public String getSoapUrl() {
        return soapUrl;
    }

    public void setSoapUrl(String soapUrl) {
        this.soapUrl = soapUrl;
    }
}
