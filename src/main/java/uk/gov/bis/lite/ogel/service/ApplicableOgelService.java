package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public class ApplicableOgelService {

  private final SpireOgelService spireOgelService;

  @Inject
  public ApplicableOgelService(SpireOgelService spireOgelService) {
    this.spireOgelService = spireOgelService;
  }

  public List<SpireOgel> findOgel(String controlCode, List<String> destinationCountries, List<ActivityType> activityTypes) {
    return SpireOgelFilter.filterSpireOgels(spireOgelService.getAllOgels(), controlCode, destinationCountries, activityTypes);
  }
}
