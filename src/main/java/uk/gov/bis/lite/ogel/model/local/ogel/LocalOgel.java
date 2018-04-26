package uk.gov.bis.lite.ogel.model.local.ogel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalOgel {

  private String id;
  private String name;
  private LocalOgelConditionSummary summary;

  public LocalOgel() {
  }

  @JsonCreator
  public LocalOgel(@JsonProperty("name") String name, @JsonProperty("summary") LocalOgelConditionSummary summary) {
    this.name = name;
    this.summary = summary;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalOgelConditionSummary getSummary() {
    return summary;
  }

  public void setSummary(LocalOgelConditionSummary summary) {
    this.summary = summary;
  }
}
