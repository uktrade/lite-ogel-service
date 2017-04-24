package uk.gov.bis.lite.ogel.service;

import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public interface ApplicableOgelService {
  List<SpireOgel> findOgel(String controlCode, List<String> destinationCountries, List<ActivityType> activityTypes);
}
