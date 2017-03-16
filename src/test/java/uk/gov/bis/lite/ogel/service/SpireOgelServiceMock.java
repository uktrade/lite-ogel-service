package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.Collections;
import java.util.List;

public class SpireOgelServiceMock implements SpireOgelService {

  private final SpireOgel ogel;

  @Inject
  public SpireOgelServiceMock() {
    SpireOgel ogel = new SpireOgel();
    ogel.setId("EXISTING");
    ogel.setName("Test OGEL");
    ogel.setLink("www.test.com/ogel");
    this.ogel = ogel;
  }

  @Override
  public List<SpireOgel> getAllOgels() {
    return Collections.singletonList(ogel);
  }

  @Override
  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    //TODO not testing filtering logic!!! Abstraction should be a provider of SpireOgels (e.g. get a collection)
    if ("EXISTING".equals(id)) {
      return ogel;
    } else {
      throw new OgelNotFoundException(id);
    }
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return null;
  }
}
