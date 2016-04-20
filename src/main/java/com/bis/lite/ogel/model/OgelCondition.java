package com.bis.lite.ogel.model;

import java.io.Serializable;
import java.util.List;

public class OgelCondition implements Serializable{


    private int id;
    private List<Rating> ratingList;
    private List<Country> includedCountries;
    private List<Country> excludedCountries;
    private List<Rating> secondaryRatingList;

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

    public List<Country> getIncludedCountries() {
        return includedCountries;
    }

    public void setIncludedCountries(List<Country> includedCountries) {
        this.includedCountries = includedCountries;
    }

    public List<Country> getExcludedCountries() {
        return excludedCountries;
    }

    public void setExcludedCountries(List<Country> excludedCountries) {
        this.excludedCountries = excludedCountries;
    }

    public List<Rating> getSecondaryRatingList() {
        return secondaryRatingList;
    }

    public void setSecondaryRatingList(List<Rating> secondaryRatingList) {
        this.secondaryRatingList = secondaryRatingList;
    }

    @Override
    public String toString() {
        return "OgelCondition{" +
                "id=" + id +
                ", ratingList=" + ratingList +
                ", includedCountries=" + includedCountries +
                ", excludedCountries=" + excludedCountries +
                ", secondaryRatingList=" + secondaryRatingList +
                '}';
    }
}
