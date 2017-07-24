package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;

@Singleton
public class ApplicableOgelServiceMock implements ApplicableOgelService {
  private final SpireOgelService spireOgelService;
  private boolean ogelFound;
  private boolean validActivityType;

  @Inject
  public ApplicableOgelServiceMock(SpireOgelService spireOgelService) {
    this.spireOgelService = spireOgelService;
    this.ogelFound = true;
    this.validActivityType = true;
  }

  @Override
  public List<SpireOgel> findOgel(String controlCode, List<String> destinationCountries, List<ActivityType> activityTypes) {
    if (validActivityType){
      if (ogelFound) {
        // Using SpireOgelServiceMock to generate the OGEL
        return Collections.singletonList(spireOgelService.findSpireOgelById("OGl1").get());
      } else {
        return Collections.emptyList();
      }
    } else {
      throw new WebApplicationException("Invalid activityType: INVALID", 400);
    }
  }

  public ApplicableOgelServiceMock setOgelFound(boolean ogelFound) {
    this.ogelFound = ogelFound;
    return this;
  }

  public ApplicableOgelServiceMock setValidActivityType(boolean validActivityType) {
    this.validActivityType = validActivityType;
    return this;
  }
}
