package uk.gov.bis.lite.ogel.model.local.ogel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalOgelLookUp {

  @JsonProperty(value = "ogelId")
  private String id;

  private String name;

  public String getId() {
    return "OGL" + id;
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
}
