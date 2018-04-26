package uk.gov.bis.lite.ogel.model.local.ogel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalControlCodeCondition {
  private String ogelID;
  private String controlCode;
  private String conditionDescription;
  private List<String> conditionDescriptionControlCodes;
  private boolean itemsAllowed;

  public String getOgelID() {
    return ogelID;
  }

  public void setOgelID(String ogelID) {
    this.ogelID = ogelID;
  }

  public String getControlCode() {
    return controlCode;
  }

  public void setControlCode(String controlCode) {
    this.controlCode = controlCode;
  }

  public String getConditionDescription() {
    return conditionDescription;
  }

  public void setConditionDescription(String conditionDescription) {
    this.conditionDescription = conditionDescription;
  }

  public List<String> getConditionDescriptionControlCodes() {
    return conditionDescriptionControlCodes;
  }

  public void setConditionDescriptionControlCodes(List<String> conditionDescriptionControlCodes) {
    this.conditionDescriptionControlCodes = conditionDescriptionControlCodes;
  }

  public boolean isItemsAllowed() {
    return itemsAllowed;
  }

  public void setItemsAllowed(boolean itemsAllowed) {
    this.itemsAllowed = itemsAllowed;
  }
}
