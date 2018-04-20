package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicableOgelServiceImpl implements ApplicableOgelService {

  private final SpireOgelService spireOgelService;

  @Inject
  public ApplicableOgelServiceImpl(SpireOgelService spireOgelService) {
    this.spireOgelService = spireOgelService;
  }

  @Override
  public List<SpireOgel> findOgel(String controlCode, List<String> destinationCountries,
                                  List<ActivityType> activityTypes) {
    return spireOgelService.getAllOgels().stream()
        .filter(
            ogel -> applyActivityTypes(ogel, activityTypes) &&
                ogel.getOgelConditions().stream()
                    .anyMatch(condition -> applyRatingIsIncluded(condition, controlCode)
                        && !applyExcludedCountriesIfPresent(condition, destinationCountries)
                        && applyIncludedCountriesIfPresent(condition, destinationCountries)
                    )
        )
        .collect(Collectors.toList());
  }

  private static boolean countryMatchesDestination(Country country, List<String> destinations) {
    return destinations.stream().anyMatch(s -> s.equalsIgnoreCase(country.getId()));
  }

  private static boolean applyRatingIsIncluded(OgelCondition condition, String rating) {
    return condition.getRatingList().stream()
        .anyMatch(conditionRating -> conditionRating.getRatingCode().equalsIgnoreCase(rating));
  }

  private static boolean applyExcludedCountriesIfPresent(OgelCondition condition, List<String> destinationCountries) {
    return condition.getCountries(OgelCondition.CountryStatus.EXCLUDED).stream()
        .anyMatch(country -> countryMatchesDestination(country, destinationCountries));
  }

  private static boolean applyIncludedCountriesIfPresent(OgelCondition ogelCondition,
                                                         List<String> destinationCountries) {
    return ogelCondition.getCountries(OgelCondition.CountryStatus.INCLUDED).isEmpty() || destinationCountries.stream()
        .allMatch(country -> ogelCondition.getCountries(OgelCondition.CountryStatus.INCLUDED)
            .stream().anyMatch(c -> c.getId().equalsIgnoreCase(country)));
  }

  private static boolean applyActivityTypes(SpireOgel ogel, List<ActivityType> activityTypes) {
    return ogel.getActivityType() == null || activityTypes.stream()
        .anyMatch(activityType -> activityType == ogel.getActivityType());
  }
}
