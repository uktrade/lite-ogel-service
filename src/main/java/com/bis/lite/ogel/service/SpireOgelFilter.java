package com.bis.lite.ogel.service;

import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.OgelCondition;
import com.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilter {

    public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList, String rating,
                                                   String destinationCountryId, List<CategoryType> categorites) {
        return ogelsList.stream().filter(
                ogel -> (applyRatingIsIncluded(ogel, rating)) &&
                        categorites.contains(ogel.getCategory()) &&
                        (applyExcludedCountriesIfPresent(ogel, destinationCountryId)
                                || applyIncludedCountriesIfPresent(ogel, destinationCountryId)))
                .collect(Collectors.toList());
    }

    private static final boolean countryMatchesDestination(Country country, String destination) {
        return country.getId().equalsIgnoreCase(destination);
    }

    private static final boolean applyRatingIsIncluded(SpireOgel ogel, String rating) {
        return ogel.getOgelConditions().stream().map(OgelCondition::getRatingList).flatMap(l -> l.stream())
                .anyMatch(r -> r.getRatingCode().equalsIgnoreCase(rating));
    }

    private static final boolean applyExcludedCountriesIfPresent(SpireOgel ogel, String destinationCountry) {
        return ogel.getOgelConditions().stream().map(OgelCondition::getExcludedCountries).count() > 0 &&
                ogel.getOgelConditions().stream().map(OgelCondition::getExcludedCountries)
                        .flatMap(l -> l.stream()).noneMatch(c -> countryMatchesDestination(c, destinationCountry));
    }

    private static final boolean applyIncludedCountriesIfPresent(SpireOgel ogel, String destinationCountry) {
        return (ogel.getOgelConditions().stream().map(OgelCondition::getIncludedCountries)
                .flatMap(l -> l.stream()).anyMatch(c -> countryMatchesDestination(c, destinationCountry)));
    }
}
