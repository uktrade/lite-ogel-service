package uk.gov.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AdditionalSpecifications {

  private String clauseText;
  private List<String> specificationText;
  private List<String> specificationControlCodes;

  @JsonCreator
  public AdditionalSpecifications(@JsonProperty("clauseText") String clauseText,
                                  @JsonProperty("specificationText") List<String> specificationText,
                                  @JsonProperty("specificationControlCodes") List<String> specificationControlCodes) {
    this.clauseText = clauseText;
    this.specificationText = specificationText;
    this.specificationControlCodes = specificationControlCodes;
  }

  public String getClauseText() {
    return clauseText;
  }

  public void setClauseText(String clauseText) {
    this.clauseText = clauseText;
  }

  public List<String> getSpecificationText() {
    return specificationText;
  }

  public void setSpecificationText(List<String> specificationText) {
    this.specificationText = specificationText;
  }

  public List<String> getSpecificationControlCodes() {
    return specificationControlCodes;
  }

  public void setSpecificationControlCodes(List<String> specificationControlCodes) {
    this.specificationControlCodes = specificationControlCodes;
  }
}
