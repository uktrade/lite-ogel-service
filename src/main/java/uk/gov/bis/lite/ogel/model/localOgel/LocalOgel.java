package uk.gov.bis.lite.ogel.model.localOgel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.gov.bis.lite.ogel.database.unmarshall.LocalOgelLookUpDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = LocalOgelLookUpDeserializer.class)
public class LocalOgel {

  private String id;
  private String name;
  private OgelSummary summary;

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

  public OgelSummary getSummary() {
    return summary;
  }

  public void setSummary(OgelSummary summary) {
    this.summary = summary;
  }
}
