package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlCodeConditionFullView {

  private String ogelId;
  private String controlCode;
  private String conditionDescription;
  private ConditionDescriptionControlCodes conditionDescriptionControlCodes;
  private boolean isItemsAllowed;

  public String getOgelId() {
    return ogelId;
  }

  public void setOgelId(String ogelId) {
    this.ogelId = ogelId;
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

  public ConditionDescriptionControlCodes getConditionDescriptionControlCodes() {
    return conditionDescriptionControlCodes;
  }

  public void setConditionDescriptionControlCodes(ConditionDescriptionControlCodes conditionDescriptionControlCodes) {
    this.conditionDescriptionControlCodes = conditionDescriptionControlCodes;
  }

  public boolean isItemsAllowed() {
    return isItemsAllowed;
  }

  public void setItemsAllowed(boolean itemsAllowed) {
    isItemsAllowed = itemsAllowed;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ConditionDescriptionControlCodes {
    private List<ControlCode> controlCodes;
    private List<String> missingControlCodes;

    public List<ControlCode> getControlCodes() {
      return controlCodes;
    }

    public void setControlCodes(List<ControlCode> controlCodeCutDowns) {
      this.controlCodes = controlCodeCutDowns;
    }

    public List<String> getMissingControlCodes() {
      return missingControlCodes;
    }

    public void setMissingControlCodes(List<String> missingControlCodes) {
      this.missingControlCodes = missingControlCodes;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ControlCode {
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

}
