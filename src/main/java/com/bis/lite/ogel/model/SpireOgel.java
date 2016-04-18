package com.bis.lite.ogel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

public class SpireOgel implements Serializable {

    private String id;
    private String description;
    private String link;
    private List<String> ratingCodes;
    private List<Country> includedCountries;
    private List<Country> excludedCountries;
    private String category;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @JsonIgnore
    public List<String> getRatingCodes() {
        return ratingCodes;
    }

    public void setRatingCodes(List<String> ratingCodes) {
        this.ratingCodes = ratingCodes;
    }

    @JsonIgnore
    public List<Country> getIncludedCountries() {
        return includedCountries;
    }

    public void setIncludedCountries(List<Country> includedCountries) {
        this.includedCountries = includedCountries;
    }

    @JsonIgnore
    public List<Country> getExcludedCountries() {
        return excludedCountries;
    }

    public void setExcludedCountries(List<Country> excludedCountries) {
        this.excludedCountries = excludedCountries;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "SpireOgel{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", ratingCodes=" + ratingCodes +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpireOgel spireOgel = (SpireOgel) o;

        if (!id.equals(spireOgel.id)) return false;
        return description.equals(spireOgel.description);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
