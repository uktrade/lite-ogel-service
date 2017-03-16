package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OgelFullView {

  private String id;
  private String name;
  private OgelConditionSummary summary;
  private String description;
  private String link;

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSummary(OgelConditionSummary summary) {
    this.summary = summary;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getId() {
    return id;
  }

  public String getName2() {
    return name;
  }

  public OgelConditionSummary getSummary() {
    return summary;
  }

  public String getDescription() {
    return description;
  }

  public String getLink() {
    return link;
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
