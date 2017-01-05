package uk.gov.bis.lite.ogel.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ValidateView {

  private List<String> unmatchedLocalOgelIds;

  private List<String> unmatchedSpireOgelIds;

  private Map<String, Collection<String>> unmatchedControlCodes;

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

  public Map<String, Collection<String>> getUnmatchedControlCodes() {
    return unmatchedControlCodes;
  }

  public void setUnmatchedControlCodes(Map<String, Collection<String>> unmatchedControlCodes) {
    this.unmatchedControlCodes = unmatchedControlCodes;
  }
}
