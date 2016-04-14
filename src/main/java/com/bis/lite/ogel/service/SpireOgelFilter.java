package com.bis.lite.ogel.service;

import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilter {

    public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList, String controlCode, String destinationCountryId) {
        return ogelsList.stream().filter(
                ogel -> (ogel.getRatingCodes().contains(controlCode)) &&
                        ((!ogel.getExcludedCountries().isEmpty() &&
                                ogel.getExcludedCountries().stream().noneMatch(c -> countryMatchesDestination(c, destinationCountryId)))
                                || (!ogel.getIncludedCountries().isEmpty() &&
                                ogel.getIncludedCountries().stream().anyMatch(c -> countryMatchesDestination(c, destinationCountryId)))))
                .collect(Collectors.toList());
    }

    private static final Boolean countryMatchesDestination(Country country, String destination) {
        return country.getId().equalsIgnoreCase(destination);
    }
}
