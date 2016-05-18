package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;

@JsonPropertyOrder({"id", "description", "link", "summary"})
public class OgelFullView {

  private final SpireOgel spireOgel;
  private final LocalOgel localOgel;

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

  @JsonProperty("id")
  public String getOgelId(){
    return spireOgel.getId();
  }

  @JsonProperty("summary")
  public OgelSummary getSummary(){
    return localOgel.getSummary();
  }

  @JsonProperty("description")
  public String getDescription(){
    return spireOgel.getDescription();
  }

  @JsonProperty("link")
  public String getPdfLink(){
    return spireOgel.getLink();
  }
}
