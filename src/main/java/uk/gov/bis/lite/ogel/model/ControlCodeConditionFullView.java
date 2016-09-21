package uk.gov.bis.lite.ogel.model;

import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.List;

public class ControlCodeConditionFullView {
  private LocalControlCodeCondition localControlCodeCondition;
  private List<ControlCodeCutDown> controlCodeCutDownList;

  public ControlCodeConditionFullView(LocalControlCodeCondition localControlCodeCondition, List<ControlCodeCutDown> controlCodeCutDownList) {
    this.localControlCodeCondition = localControlCodeCondition;
    this.controlCodeCutDownList = controlCodeCutDownList;
  }

  public String getOgelID() {
    return localControlCodeCondition.getOgelID();
  }

  public String getControlCode() {
    return localControlCodeCondition.getControlCode();
  }

  public String getConditionDescription() {
    return localControlCodeCondition.getConditionDescription();
  }

  public List<ControlCodeCutDown> getConditionDescriptionControlCodes() {
    return controlCodeCutDownList;
  }

  public boolean isItemsAllowed() {
    return localControlCodeCondition.isItemsAllowed();
  }
}
