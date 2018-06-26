package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateView {

  private List<String> unmatchedLocalOgelIds;

  private List<String> unmatchedSpireOgelIds;

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

}
