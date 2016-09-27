package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BulkControlCodeCutDowns {
  private List<ControlCodeCutDown> controlCodes;
  private List<String> missingControlCodes;

  @JsonCreator
  public BulkControlCodeCutDowns(@JsonProperty("controlCodeFullViews") List<ControlCodeCutDown> controlCodes, @JsonProperty("missingControlCodes") List<String> missingControlCodes) {
    this.controlCodes = controlCodes;
    this.missingControlCodes = missingControlCodes;
  }

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