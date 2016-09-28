package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilter {

  public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList, String rating,
                                                 String destinationCountryId, List<ActivityType> activityTypes) {
    return ogelsList.stream().filter(ogel ->
            applyActivityTypes(ogel, activityTypes) && ogel.getOgelConditions().stream()
                .anyMatch(ogelCondition ->
                    applyRatingIsIncluded(ogelCondition, rating)
                    && !applyExcludedCountriesIfPresent(ogelCondition, destinationCountryId)
                    && applyIncludedCountriesIfPresent(ogelCondition, destinationCountryId)
                )).collect(Collectors.toList());
  }

  private static boolean countryMatchesDestination(Country country, String destination) {
    return country.getId().equalsIgnoreCase(destination);
  }

  private static boolean applyRatingIsIncluded(OgelCondition ogelCondition, String rating) {
    return ogelCondition.getRatingList().stream()
        .anyMatch(conditionRating -> conditionRating.getRatingCode().equalsIgnoreCase(rating));
  }

  private static boolean applyExcludedCountriesIfPresent(OgelCondition ogelCondition, String destinationCountry) {
    return ogelCondition.getExcludedCountries().stream()
        .anyMatch(country -> countryMatchesDestination(country, destinationCountry));
  }

  private static boolean applyIncludedCountriesIfPresent(OgelCondition ogelCondition, String destinationCountry) {
    return ogelCondition.getIncludedCountries().isEmpty() || ogelCondition.getIncludedCountries().stream()
        .anyMatch(country -> countryMatchesDestination(country, destinationCountry));
  }

  private static boolean applyActivityTypes(SpireOgel ogel, List<ActivityType> activityTypes) {
    return ogel.getActivityType() == null || activityTypes.stream()
        .anyMatch(activityType -> activityType == ogel.getActivityType());
  }
}
