package uk.gov.bis.lite.ogel.model;

import java.io.Serializable;

public class Rating implements Serializable {

  private String ratingCode;
  private boolean conditionalRating; // retrieved from Spire but not currently being used in Ogel Service logic

  public Rating() {
  }

  public Rating(String code) {
    this.ratingCode = code;
  }

  public Rating(String code, boolean conditional) {
    this.ratingCode = code;
    this.conditionalRating = conditional;
  }

  public String getRatingCode() {
    return ratingCode;
  }

  public void setRatingCode(String ratingCode) {
    this.ratingCode = ratingCode;
  }

  public void setConditionalRating(boolean conditionalRating) {
    this.conditionalRating = conditionalRating;
  }

  @Override
  public String toString() {
    return "Rating{" +
        "ratingCode='" + ratingCode + '\'' +
        ", conditionalRating=" + conditionalRating +
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

    Rating rating = (Rating) o;

    if (conditionalRating != rating.conditionalRating) {
      return false;
    }
    return ratingCode != null ? ratingCode.equals(rating.ratingCode) : rating.ratingCode == null;

  }

  @Override
  public int hashCode() {
    int result = ratingCode != null ? ratingCode.hashCode() : 0;
    result = 31 * result + (conditionalRating ? 1 : 0);
    return result;
  }
}
