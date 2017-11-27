package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpireOgel {

  @JsonIgnore
  private String id;
  private String name;
  private String link;
  private List<OgelCondition> ogelConditions;
  @JsonIgnore
  private ActivityType activityType;
  private int ranking;
  private String lastUpdatedDate;

  public String getId() {
    return id;
  }

  public SpireOgel setId(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public ActivityType getActivityType() {
    return activityType;
  }

  public void setActivityType(ActivityType activityType) {
    this.activityType = activityType;
  }

  @JsonIgnore
  public List<OgelCondition> getOgelConditions() {
    return ogelConditions;
  }

  public void setOgelConditions(List<OgelCondition> ogelConditions) {
    this.ogelConditions = ogelConditions;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public String getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setLastUpdatedDate(String lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }
}
