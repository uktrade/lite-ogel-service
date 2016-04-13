package com.bis.lite.ogel.util;

import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public class SpireOgelTestUtility {

    public static SpireOgel createOgel(String code, String description, List<Country> includedCountries, List<Country> excludedCountries){
        SpireOgel spireOgel = new SpireOgel();
        spireOgel.setOgelCode(code);
        spireOgel.setDescription(description);
        spireOgel.setIncludedCountries(includedCountries);
        spireOgel.setExcludedCountries(excludedCountries);
        return spireOgel;
    }
}
