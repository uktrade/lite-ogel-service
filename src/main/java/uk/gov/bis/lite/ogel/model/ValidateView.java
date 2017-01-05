package uk.gov.bis.lite.ogel.model;

import java.util.List;

public class ValidateView {

  private List<String> unmatchedLocalOgelIds;

  private List<String> unmatchedSpireOgelIds;

  private List<String> unmatchedControlCodes;

  public List<String> getUnmatchedLocalOgelIds() {
    return unmatchedLocalOgelIds;
  }

  public void setUnmatchedLocalOgelIds(List<String> unmatchedLocalOgelIds) {
    this.unmatchedLocalOgelIds = unmatchedLocalOgelIds;
  }

  public List<String> getUnmatchedSpireOgelIds() {
    return unmatchedSpireOgelIds;
  }

  public void setUnmatchedSpireOgelIds(List<String> unmatchedSpireOgelIds) {
    this.unmatchedSpireOgelIds = unmatchedSpireOgelIds;
  }

  public List<String> getUnmatchedControlCodes() {
    return unmatchedControlCodes;
  }

  public void setUnmatchedControlCodes(List<String> unmatchedControlCodes) {
    this.unmatchedControlCodes = unmatchedControlCodes;
  }
}
