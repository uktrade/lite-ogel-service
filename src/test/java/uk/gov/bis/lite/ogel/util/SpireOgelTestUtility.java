package uk.gov.bis.lite.ogel.util;

import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public class SpireOgelTestUtility {

  public static SpireOgel createOgel(String code, String name, List<OgelCondition> ogelConditions, CategoryType category) {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setOgelConditions(ogelConditions);
    spireOgel.setId(code);
    spireOgel.setName(name);
    spireOgel.setCategory(category);
    return spireOgel;
  }

  public static Rating createRating(String code, boolean conditional) {
    return new Rating(code, conditional);
  }

  public static OgelCondition createCondition(List<Rating> ratings, List<Country> excludedCountries, List<Country> includedCountries) {
    OgelCondition ogelCondition = new OgelCondition();
    ogelCondition.setRatingList(ratings);
    ogelCondition.setExcludedCountries(excludedCountries);
    ogelCondition.setIncludedCountries(includedCountries);
    return ogelCondition;
  }
}
