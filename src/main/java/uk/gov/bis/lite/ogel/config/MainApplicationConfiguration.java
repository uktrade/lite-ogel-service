package uk.gov.bis.lite.ogel.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MainApplicationConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database;

  @NotEmpty
  @JsonProperty
  private String cacheTimeout;

  @NotEmpty
  @JsonProperty
  private String adminLogin;

  @NotEmpty
  @JsonProperty
  private String adminPassword;

  @NotEmpty
  @JsonProperty
  private String serviceLogin;

  @NotEmpty
  @JsonProperty
  private String servicePassword;

  @NotEmpty
  @JsonProperty
  private String virtualEuOgelId;

  @NotEmpty
  @JsonProperty
  private String spireClientUserName;

  @NotEmpty
  @JsonProperty
  private String spireClientPassword;

  @NotEmpty
  @JsonProperty
  private String spireClientUrl;

  @NotEmpty
  @JsonProperty
  private String spireOgelCacheJobCron;

  @Valid
  @NotNull
  @JsonProperty("jerseyClient")
  private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

  @Valid
  @NotNull
  public JerseyClientConfiguration getJerseyClientConfiguration() {
    return jerseyClient;
  }

  @NotEmpty
  @JsonProperty
  private String controlCodeServiceUrl;

  public DataSourceFactory getDatabase() {
    return database;
  }

  public String getCacheTimeout() {
    return cacheTimeout;
  }

  public String getAdminLogin() {
    return adminLogin;
  }

  public String getAdminPassword() {
    return adminPassword;
  }

  public String getServiceLogin() {
    return serviceLogin;
  }

  public String getServicePassword() {
    return servicePassword;
  }

  public String getVirtualEuOgelId() {
    return virtualEuOgelId;
  }

  public String getSpireClientUserName() {
    return spireClientUserName;
  }

  public String getSpireClientPassword() {
    return spireClientPassword;
  }

  public String getSpireClientUrl() {
    return spireClientUrl;
  }

  public String getSpireOgelCacheJobCron() {
    return spireOgelCacheJobCron;
  }

  public JerseyClientConfiguration getJerseyClient() {
    return jerseyClient;
  }

  public String getControlCodeServiceUrl() {
    return controlCodeServiceUrl;
  }

}
