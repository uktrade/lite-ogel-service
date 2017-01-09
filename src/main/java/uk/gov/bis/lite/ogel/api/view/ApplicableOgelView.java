package uk.gov.bis.lite.ogel.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicableOgelView {

  private String id;
  private String name;
  private List<String> usageSummary;

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

  public List<String> getUsageSummary() {
    return usageSummary;
  }

  public void setUsageSummary(List<String> usageSummary) {
    this.usageSummary = usageSummary;
  }
}
