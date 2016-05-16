package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpireOgel implements Serializable {

  @JsonIgnore
  private String id;
  private String description;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SpireOgel spireOgel = (SpireOgel) o;

    return id.equals(spireOgel.id) && description.equals(spireOgel.description);

  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + description.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "SpireOgel{" +
        "id='" + id + '\'' +
        ", description='" + description + '\'' +
        ", link='" + link + '\'' +
        ", ogelConditions=" + ogelConditions +
        ", category='" + category + '\'' +
        '}';
  }
}
