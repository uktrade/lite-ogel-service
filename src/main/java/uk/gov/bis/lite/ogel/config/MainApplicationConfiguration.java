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

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @NotEmpty
  @JsonProperty
  private String cacheTimeout;

  @NotEmpty
  @JsonProperty
  private String login;

  @NotEmpty
  @JsonProperty
  private String password;

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

  public String getSpireClientUserName() {
    return spireClientUserName;
  }

  public String getSpireClientPassword() {
    return spireClientPassword;
  }

  public String getSpireClientUrl() {
    return spireClientUrl;
  }

  public String getCacheTimeout() {
    return cacheTimeout;
  }

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }

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

  public String getControlCodeServiceUrl() {
    return controlCodeServiceUrl;
  }

  public String getVirtualEuOgelId() {
    return virtualEuOgelId;
  }

  public String getSpireOgelCacheJobCron() {
    return spireOgelCacheJobCron;
  }
}
