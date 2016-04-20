package com.bis.lite.ogel.util;

import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.OgelCondition;
import com.bis.lite.ogel.model.Rating;
import com.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public class SpireOgelTestUtility {

    public static SpireOgel createOgel(String code, String description, List<OgelCondition> ogelConditions, CategoryType category){
        SpireOgel spireOgel = new SpireOgel();
        spireOgel.setOgelConditions(ogelConditions);
        spireOgel.setId(code);
        spireOgel.setDescription(description);
        spireOgel.setCategory(category);
        return spireOgel;
    }

    public static Rating createRating(String code, boolean conditional){
        return new Rating(code, conditional);
    }

    public static OgelCondition createCondition(List<Rating> ratings, List<Country> excludedCountries, List<Country> includedCountries){
        OgelCondition ogelCondition = new OgelCondition();
        ogelCondition.setRatingList(ratings);
        ogelCondition.setExcludedCountries(excludedCountries);
        ogelCondition.setIncludedCountries(includedCountries);
        return ogelCondition;
    }
}
