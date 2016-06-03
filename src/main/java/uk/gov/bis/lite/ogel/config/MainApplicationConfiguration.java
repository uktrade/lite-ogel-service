package uk.gov.bis.lite.ogel.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MainApplicationConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database = new DataSourceFactory();

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @NotEmpty
  @JsonProperty
  private String soapUrl;

  @NotEmpty
  @JsonProperty
  private String soapUserName;

  @NotEmpty
  @JsonProperty
  private String soapPassword;

  @NotEmpty
  @JsonProperty
  private String cacheTimeout;

  @NotEmpty
  @JsonProperty
  private String login;

  @NotEmpty
  @JsonProperty
  private String password;


  public String getSoapUrl() {
    return soapUrl;
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

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }
}
