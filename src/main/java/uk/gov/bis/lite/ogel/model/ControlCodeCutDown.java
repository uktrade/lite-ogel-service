package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlCodeCutDown {
  private String id;
  private String controlCode;
  private String friendlyDescription;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getControlCode() {
    return controlCode;
  }

  public void setControlCode(String controlCode) {
    this.controlCode = controlCode;
  }

  public String getFriendlyDescription() {
    return friendlyDescription;
  }

  public void setFriendlyDescription(String friendlyDescription) {
    this.friendlyDescription = friendlyDescription;
  }
}
