package uk.gov.bis.lite.ogel.util;

import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtil {

  public static String OGLW = "OGLW";
  public static String OGLX = "OGLX";
  public static String OGLY = "OGLY";
  public static String OGLZ = "OGLZ";
  public static String OGLMIX = "OGLMIX";
  public static String OGLEU = "OGL61"; // Virtual EU ogel
  public static String OGL_ = "OGL_"; // This Ogel does not exist
  public static String OGLTEMP = "OGLTEMP";

  public static String RAT1 = "rat1";
  public static String RAT2 = "rat2";
  public static String RAT3 = "rat3";
  public static String RAT4 = "rat4";
  public static String RAT5 = "rat5";
  public static String RATA = "ratA";
  public static String RATB = "ratB";
  public static String RATX = "ratx";

  private static ActivityType TECH = ActivityType.TECH;
  private static ActivityType REPAIR = ActivityType.REPAIR;

  private static Map<Integer, Country> countryMap = new HashMap<Integer, Country>();

  static {
    countryMap.put(1, new Country("1", "11", "Country1"));
    countryMap.put(2, new Country("2", "22", "Country2"));
    countryMap.put(3, new Country("3", "33", "Country3"));
    countryMap.put(4, new Country("4", "44", "Country4"));
    countryMap.put(5, new Country("5", "55", "Country5"));
    countryMap.put(6, new Country("6", "66", "Country6"));
  }

  public static SpireOgel ogelW() {
    return ogel(OGLW, conditions(ratings(RAT1, RAT2, RAT3), countries(1, 2, 3), countries(4)), TECH, 1);
  }

  public static SpireOgel ogelX() {
    return ogel(OGLX, conditions(ratings(RAT1, RAT2, RAT3), countries(1, 2, 3), countries(4)), TECH, 1);
  }

  public static SpireOgel ogelY() {
    return ogel(OGLY, conditions(ratings(RAT1, RAT2, RAT4, RAT5), countries(1, 2), countries(3, 4)), REPAIR, 2);
  }

  public static SpireOgel ogelZ() {
    return ogel(OGLZ, conditions(ratings(RAT1, RAT5), countries(1), countries(2, 3, 4)), TECH, 3);
  }

  public static SpireOgel ogelMix() {
    OgelCondition condition1 = createCondition(ratings(RATA), countries(5), OgelCondition.CountryStatus.INCLUDED);
    OgelCondition condition2 = createCondition(ratings(RATB), countries(6), OgelCondition.CountryStatus.INCLUDED);
    return ogel(OGLMIX, Arrays.asList(condition1, condition2), TECH, 999);
  }

  public static SpireOgel ogelEU() {
    return ogel(OGLEU, conditions(ratings(RAT1, RAT2, RAT3), countries(1, 2, 3), countries(4)), TECH, 999);
  }

  public static LocalOgel localX() {
    LocalOgel ogel = new LocalOgel();
    ogel.setId(OGLX);
    OgelConditionSummary summary = new OgelConditionSummary();
    summary.setCanList(Arrays.asList("can1", "can2", "can3"));
    summary.setCantList(Arrays.asList("cannot1", "cannot2"));
    summary.setMustList(Arrays.asList("must1", "must2"));
    summary.setHowToUseList(Arrays.asList("how1", "how2"));
    ogel.setSummary(summary);
    return ogel;
  }

  public static SpireOgel invalidOgel() {
    SpireOgel ogel = new SpireOgel();
    ogel.setLink("link");
    return ogel;
  }

  public static List<ActivityType> activities() {
    return Arrays.asList(ActivityType.TECH, ActivityType.REPAIR);
  }

  public static List<ActivityType> tech() {
    return Collections.singletonList(ActivityType.TECH);
  }

  public static List<ActivityType> repair() {
    return Collections.singletonList(ActivityType.REPAIR);
  }

  public static List<String> countryIds(int... countryMapKeys) {
    List<String> countries = new ArrayList<>();
    for (int key : countryMapKeys) {
      countries.add("" + key);
    }
    return countries;
  }

  public static <T> List<T> list() {
    return new ArrayList<>();
  }

  private static SpireOgel ogel(String code, List<OgelCondition> conditions, ActivityType activity, int ranking) {
    SpireOgel ogel = new SpireOgel();
    ogel.setOgelConditions(conditions);
    ogel.setId(code);
    ogel.setName("Name" + code);
    ogel.setActivityType(activity);
    ogel.setRanking(ranking);
    return ogel;
  }

  private static List<OgelCondition> conditions(List<Rating> ratings, List<Country> includedCountries, List<Country> excludedCountries) {
    OgelCondition condition1 = createCondition(ratings, includedCountries, OgelCondition.CountryStatus.INCLUDED);
    OgelCondition condition2 = createCondition(ratings, excludedCountries, OgelCondition.CountryStatus.EXCLUDED);
    return Arrays.asList(condition1, condition2);
  }

  private static OgelCondition createCondition(List<Rating> ratings, List<Country> countries, OgelCondition.CountryStatus status) {
    OgelCondition condition = new OgelCondition();
    condition.setRatingList(ratings);
    condition.setCountries(countries, status);
    return condition;
  }

  private static List<Rating> ratings(String... codes) {
    List<Rating> ratings = new ArrayList<>();
    for (String code : codes) {
      ratings.add(new Rating(code));
    }
    return ratings;
  }

  private static List<Country> countries(int... countryMapKeys) {
    List<Country> countries = new ArrayList<>();
    for (int key : countryMapKeys) {
      countries.add(countryMap.get(key));
    }
    return countries;
  }
}
