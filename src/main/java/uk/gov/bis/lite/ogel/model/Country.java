package uk.gov.bis.lite.ogel.model;

import java.io.Serializable;

public class Country implements Serializable {

  private String id;
  private String setID;
  private String name;

  public Country() {
  }

  public Country(String id, String setID, String name) {
    this.id = id;
    this.setID = setID;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSetID() {
    return setID;
  }

  public void setSetID(String setID) {
    this.setID = setID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Country{" +
        "id='" + id + '\'' +
        ", setID='" + setID + '\'' +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Country country = (Country) o;

    if (id != null ? !id.equals(country.id) : country.id != null) {
      return false;
    }
    if (setID != null ? !setID.equals(country.setID) : country.setID != null) {
      return false;
    }
    return name != null ? name.equals(country.name) : country.name == null;

  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (setID != null ? setID.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
