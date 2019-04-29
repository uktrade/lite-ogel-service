package uk.gov.bis.lite.ogel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OgelCondition implements Serializable {

  private int id;
  private List<Rating> ratingList;
  private List<Country> countries;
  private CountryStatus countryStatus;

  public enum CountryStatus {
    INCLUDED, EXCLUDED
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Rating> getRatingList() {
    return ratingList;
  }

  public void setRatingList(List<Rating> ratingList) {
    this.ratingList = ratingList;
  }

  public void setCountries(List<Country> countries, CountryStatus status) {
    this.countries = countries;
    this.countryStatus = status;
  }

  public List<Country> getCountries() {
    return countries;
  }

  public List<Country> getCountries(CountryStatus countryStatus) {
    if (countryStatus.equals(this.countryStatus)) {
      return countries;
    }
    return new ArrayList<>(0);
  }

  @Override
  public String toString() {
    return String.format("OgelCondition{id=%d, ratingList=%s, countries=%s, countryStatus=%s}", id, ratingList,
        countries, countryStatus.name());
  }
}
