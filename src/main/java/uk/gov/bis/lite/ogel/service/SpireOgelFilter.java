package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilter {

  public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList, String rating,
                                                 List<String> destinationCountries, List<ActivityType> activityTypes) {

    return ogelsList.stream().filter(
        ogel -> applyActivityTypes(ogel, activityTypes) && ogel.getOgelConditions().stream()
            .anyMatch(condition ->
                applyRatingIsIncluded(condition, rating)
                    && !applyExcludedCountriesIfPresent(condition, destinationCountries)
                    && applyIncludedCountriesIfPresent(condition, destinationCountries)
            )).collect(Collectors.toList());
  }

  private static boolean countryMatchesDestination(Country country, List<String> destinations) {
    return destinations.stream().filter(s -> s.equalsIgnoreCase(country.getId())).findFirst().isPresent();
  }

  private static boolean applyRatingIsIncluded(OgelCondition condition, String rating) {
    return condition.getRatingList().stream()
        .anyMatch(conditionRating -> conditionRating.getRatingCode().equalsIgnoreCase(rating));
  }

  private static boolean applyExcludedCountriesIfPresent(OgelCondition condition, List<String> destinationCountries) {
    return condition.getCountries(OgelCondition.CountryStatus.EXCLUDED).stream()
        .anyMatch(country -> countryMatchesDestination(country, destinationCountries));
  }

  private static boolean applyIncludedCountriesIfPresent(OgelCondition ogelCondition, List<String> destinationCountries) {
    return ogelCondition.getCountries(OgelCondition.CountryStatus.INCLUDED).isEmpty() || ogelCondition.getCountries().stream()
        .anyMatch(country -> countryMatchesDestination(country, destinationCountries));
  }

  private static boolean applyActivityTypes(SpireOgel ogel, List<ActivityType> activityTypes) {
    return ogel.getActivityType() == null || activityTypes.stream()
        .anyMatch(activityType -> activityType == ogel.getActivityType());
  }

  /*return ogelsList.stream().filter(
        ogel -> applyRatingIsIncluded(ogel, rating) &&
            activityTypes.contains(ogel.getActivityType()) &&
            (applyExcludedCountriesIfPresent(ogel, destinationCountries)
                && applyIncludedCountriesIfPresent(ogel, destinationCountries)))
        .collect(Collectors.toList());*/
  /*
  public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList, String rating,
                                                 String destinationCountryId, List<ActivityType> activityTypes) {
    return ogelsList.stream().filter(
        ogel -> applyActivityTypes(ogel, activityTypes) && ogel.getOgelConditions().stream()
                .anyMatch(ogelCondition ->
                    applyRatingIsIncluded(ogelCondition, rating)
                    && !applyExcludedCountriesIfPresent(ogelCondition, destinationCountryId)
                    && applyIncludedCountriesIfPresent(ogelCondition, destinationCountryId)
                )).collect(Collectors.toList());
  }
   */
  /*
  private static boolean applyIncludedCountriesIfPresent(SpireOgel ogel, List<String> destinationCountries) {
    if (ogel.getOgelConditions().stream().flatMap(oc -> oc.getIncludedCountries().stream()).count() == 0) {
      return true;
    }
    return ogel.getOgelConditions().stream()
        .flatMap(oc -> oc.getIncludedCountries().stream()).anyMatch(c -> countryMatchesDestination(c, destinationCountries));
  }*/
/*
  private static boolean applyExcludedCountriesIfPresent(SpireOgel ogel, List<String> destinationCountries) {
    return ogel.getOgelConditions().stream().
        flatMap(oc -> oc.getExcludedCountries().stream()).noneMatch(c -> countryMatchesDestination(c, destinationCountries));
  }*/
/*
  private static boolean applyRatingIsIncluded(SpireOgel ogel, String rating) {
    return ogel.getOgelConditions().stream()
        .flatMap(oc -> oc.getRatingList().stream()).anyMatch(r -> r.getRatingCode().equalsIgnoreCase(rating));
  }*/
}