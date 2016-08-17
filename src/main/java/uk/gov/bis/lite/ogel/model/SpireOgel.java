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
  private CategoryType category;

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

  public CategoryType getCategory() {
    return category;
  }

  public void setCategory(CategoryType category) {
    this.category = category;
  }

  @JsonIgnore
  public List<OgelCondition> getOgelConditions() {
    return ogelConditions;
  }

  public void setOgelConditions(List<OgelCondition> ogelConditions) {
    this.ogelConditions = ogelConditions;
  }

}
