package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlCodeFullView {

  private String id;
  private String controlCode;


  public void setId(String id) {
    this.id = id;
  }

  public void setControlCode(String controlCode) {
    this.controlCode = controlCode;
  }

  public String getId() {
    return id;
  }

  public String getControlCode() {
    return controlCode;
  }

}
