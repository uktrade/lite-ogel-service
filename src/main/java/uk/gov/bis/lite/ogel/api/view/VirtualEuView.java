package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualEuView {

  private boolean virtualEu;
  private String ogelId;

  public boolean isVirtualEu() {
    return virtualEu;
  }

  public void setVirtualEu(boolean virtualEu) {
    this.virtualEu = virtualEu;
  }

  public String getOgelId() {
    return ogelId;
  }

  public void setOgelId(String ogelId) {
    this.ogelId = ogelId;
  }

}
