package com.bis.lite.ogel.service;

import com.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

public class SpireOgelFilter {

    public static List<SpireOgel> filterSpireOgels(List<SpireOgel> ogelsList,String controlCode, String destinationCountryId){
        return ogelsList.stream().filter(
                ogel -> ogel.getOgelCode().equalsIgnoreCase(controlCode) &&
                        ogel.getExcludedCountries().stream().noneMatch(country -> country.getId().equalsIgnoreCase(destinationCountryId)))
                .collect(Collectors.toList());
    }
}
