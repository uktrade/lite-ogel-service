package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilter {

  public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList, String rating,
                                                 String destinationCountryId, List<ActivityType> categorites) {
    return ogelsList.stream().filter(
        ogel -> (applyRatingIsIncluded(ogel, rating)) &&
            categorites.contains(ogel.getActivityType()) &&
            (applyExcludedCountriesIfPresent(ogel, destinationCountryId)
                || applyIncludedCountriesIfPresent(ogel, destinationCountryId)))
        .collect(Collectors.toList());
  }

  private static boolean countryMatchesDestination(Country country, String destination) {
    return country.getId().equalsIgnoreCase(destination);
  }

  private static boolean applyRatingIsIncluded(SpireOgel ogel, String rating) {
    return ogel.getOgelConditions().stream().map(OgelCondition::getRatingList).flatMap(l -> l.stream())
        .anyMatch(r -> r.getRatingCode().equalsIgnoreCase(rating));
  }

  private static boolean applyExcludedCountriesIfPresent(SpireOgel ogel, String destinationCountry) {
    return ogel.getOgelConditions().stream().map(OgelCondition::getExcludedCountries).count() > 0 &&
        ogel.getOgelConditions().stream().map(OgelCondition::getExcludedCountries)
            .flatMap(l -> l.stream()).noneMatch(c -> countryMatchesDestination(c, destinationCountry));
  }

  private static boolean applyIncludedCountriesIfPresent(SpireOgel ogel, String destinationCountry) {
    return (ogel.getOgelConditions().stream().map(OgelCondition::getIncludedCountries)
        .flatMap(l -> l.stream()).anyMatch(c -> countryMatchesDestination(c, destinationCountry)));
  }
}
