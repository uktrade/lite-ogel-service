package com.bis.lite.ogel.util;

import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public class SpireOgelTestUtility {

    public static SpireOgel createOgel(String code, String description, List<String> ratingList, List<Country> includedCountries, List<Country> excludedCountries){
        SpireOgel spireOgel = new SpireOgel();
        spireOgel.setRatingCodes(ratingList);
        spireOgel.setId(code);
        spireOgel.setDescription(description);
        spireOgel.setIncludedCountries(includedCountries);
        spireOgel.setExcludedCountries(excludedCountries);
        spireOgel.setCategory("TECH");
        return spireOgel;
    }
}
