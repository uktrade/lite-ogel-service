package uk.gov.bis.lite.ogel.model.localOgel;

import java.util.List;

public class OgelSummary {
  private List<String> canList;
  private List<String> cantList;
  private List<String> mustList;
  private List<String> howToUseList;

  public List<String> getCanList() {
    return canList;
  }

  public void setCanList(List<String> canList) {
    this.canList = canList;
  }

  public List<String> getCantList() {
    return cantList;
  }

  public void setCantList(List<String> cantList) {
    this.cantList = cantList;
  }

  public List<String> getMustList() {
    return mustList;
  }

  public void setMustList(List<String> mustList) {
    this.mustList = mustList;
  }

  public List<String> getHowToUseList() {
    return howToUseList;
  }

  public void setHowToUseList(List<String> howToUseList) {
    this.howToUseList = howToUseList;
  }
}
