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
}