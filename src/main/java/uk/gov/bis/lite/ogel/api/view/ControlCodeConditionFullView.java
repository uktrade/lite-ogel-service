package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.gov.bis.lite.ogel.model.ControlCodeCutDown;

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

  public static class ConditionDescriptionControlCodes {
    private List<ControlCodeCutDown> controlCodes;
    private List<String> missingControlCodes;

    public List<ControlCodeCutDown> getControlCodes() {
      return controlCodes;
    }

    public void setControlCodes(List<ControlCodeCutDown> controlCodes) {
      this.controlCodes = controlCodes;
    }

    public List<String> getMissingControlCodes() {
      return missingControlCodes;
    }

    public void setMissingControlCodes(List<String> missingControlCodes) {
      this.missingControlCodes = missingControlCodes;
    }
  }

}
