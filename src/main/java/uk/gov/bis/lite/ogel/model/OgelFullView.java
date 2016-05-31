package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;

import java.io.Serializable;

@JsonPropertyOrder({"id", "description", "link", "summary"})
public class OgelFullView implements Serializable {

  private SpireOgel spireOgel;
  private LocalOgel localOgel;

  public SpireOgel getSpireOgel() {
    return spireOgel;
  }

  public LocalOgel getLocalOgel() {
    return localOgel;
  }

  @JsonProperty("id")
  public String getOgelId() {
    return spireOgel.getId();
  }

  @JsonProperty("summary")
  public OgelSummary getSummary() {
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

  public void setSpireOgel(SpireOgel spireOgel) {
    this.spireOgel = spireOgel;
  }

  public void setLocalOgel(LocalOgel localOgel) {
    this.localOgel = localOgel;
  }
}
