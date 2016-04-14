package com.bis.lite.ogel.model;

import java.io.Serializable;
import java.util.List;

public class SpireOgel implements Serializable {

    private String ogelCode;
    private String description;
    private String link;
    private List<String> ratingCodes;
    private List<Country> includedCountries;
    private List<Country> excludedCountries;

    public String getOgelCode() {
        return ogelCode;
    }

    public void setOgelCode(String ogelCode) {
        this.ogelCode = ogelCode;
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

    public List<String> getRatingCodes() {
        return ratingCodes;
    }

    public void setRatingCodes(List<String> ratingCodes) {
        this.ratingCodes = ratingCodes;
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

    @Override
    public String toString() {
        return "SpireOgel{" +
                "ogelCode='" + ogelCode + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", includedCountries=" + includedCountries + '\'' +
                ", excludedCountries=" + excludedCountries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpireOgel spireOgel = (SpireOgel) o;

        if (!ogelCode.equals(spireOgel.ogelCode)) return false;
        return description.equals(spireOgel.description);

    }

    @Override
    public int hashCode() {
        int result = ogelCode.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
