package uk.gov.bis.lite.ogel.model.local.ogel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class LocalOgelConditionSummary {
  private List<String> canList;
  private List<String> cantList;
  private List<String> mustList;
  private List<String> howToUseList;

  @JsonCreator
  public LocalOgelConditionSummary(@JsonProperty("canList") List<String> canList,
                                   @JsonProperty("cantList") List<String> cantList,
                                   @JsonProperty("mustList") List<String> mustList,
                                   @JsonProperty("howToUseList") List<String> howToUseList) {
    this.canList = canList;
    this.cantList = cantList;
    this.mustList = mustList;
    this.howToUseList = howToUseList;
  }

  public LocalOgelConditionSummary() {
    this.canList = Collections.emptyList();
    this.cantList = Collections.emptyList();
    this.mustList = Collections.emptyList();
    this.howToUseList = Collections.emptyList();
  }

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
