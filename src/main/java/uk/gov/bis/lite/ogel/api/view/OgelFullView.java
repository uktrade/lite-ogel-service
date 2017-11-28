package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OgelFullView {

  private String id;
  private String name;
  private OgelConditionSummary summary;
  private String link;

  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
  private LocalDate lastUpdatedDate;

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSummary(OgelConditionSummary summary) {
    this.summary = summary;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public OgelConditionSummary getSummary() {
    return summary;
  }

  public String getLink() {
    return link;
  }

  public LocalDate getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OgelConditionSummary {
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

}
