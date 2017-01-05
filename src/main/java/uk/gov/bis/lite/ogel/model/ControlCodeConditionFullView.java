package uk.gov.bis.lite.ogel.model;

import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

public class ControlCodeConditionFullView {
  private final LocalControlCodeCondition localControlCodeCondition;
  private final BulkControlCodeCutDowns bulkControlCodeCutDowns;

  public ControlCodeConditionFullView(LocalControlCodeCondition localControlCodeCondition, BulkControlCodeCutDowns bulkControlCodeCutDowns) {
    this.localControlCodeCondition = localControlCodeCondition;
    this.bulkControlCodeCutDowns = bulkControlCodeCutDowns;
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

  public BulkControlCodeCutDowns getConditionDescriptionControlCodes() {
    return bulkControlCodeCutDowns;
  }

  public boolean isItemsAllowed() {
    return localControlCodeCondition.isItemsAllowed();
  }
}
