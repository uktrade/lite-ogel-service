package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.io.Serializable;

@JsonPropertyOrder({"id", "name", "description", "link", "summary"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OgelFullView implements Serializable {

  private SpireOgel spireOgel;
  private LocalOgel localOgel;

  public OgelFullView(SpireOgel spireOgel, LocalOgel localOgel) {
    this.spireOgel = spireOgel;
    this.localOgel = localOgel;
  }

  @JsonIgnore
  public SpireOgel getSpireOgel() {
    return spireOgel;
  }

  @JsonIgnore
  public LocalOgel getLocalOgel() {
    return localOgel;
  }

  @JsonProperty("name")
  public String getOgelName() {
    if(localOgel == null){
      return "";
    }
    return localOgel.getName();
  }

  @JsonProperty("id")
  public String getOgelId() {
    return spireOgel.getId();
  }

  @JsonProperty("summary")
  public OgelConditionSummary getSummary() {
    if(localOgel == null){
      return new OgelConditionSummary();
    }
    return localOgel.getSummary();
  }

  @JsonProperty("description")
  public String getDescription() {
    return spireOgel.getDescription();
  }

  @JsonProperty("link")
  public String getPdfLink() {
    return spireOgel.getLink();
  }
}
