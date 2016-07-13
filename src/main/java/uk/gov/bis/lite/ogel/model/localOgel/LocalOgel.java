package uk.gov.bis.lite.ogel.model.localOgel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalOgel {

  private String id;
  private String name;
  private OgelConditionSummary summary;

  public LocalOgel() {
  }

  @JsonCreator
  public LocalOgel(@JsonProperty("name") String name, @JsonProperty("summary") OgelConditionSummary summary) {
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

  public OgelConditionSummary getSummary() {
    return summary;
  }

  public void setSummary(OgelConditionSummary summary) {
    this.summary = summary;
  }
}
