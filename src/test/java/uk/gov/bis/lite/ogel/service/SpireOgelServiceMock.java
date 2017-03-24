package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.Collections;
import java.util.List;

public class SpireOgelServiceMock implements SpireOgelService {

  private SpireOgel ogel;

  @Inject
  public SpireOgelServiceMock() {
    this.ogel = buildOgel("OGL1");
  }

  @Override
  public List<SpireOgel> getAllOgels() {
    if (ogel != null) {
      return Collections.singletonList(ogel);
    }
    return Collections.emptyList();
  }

  @Override
  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    //TODO not testing filtering logic!!! Abstraction should be a provider of SpireOgels (e.g. get a collection)
    if (ogel != null) {
      return ogel;
    } else {
      throw new OgelNotFoundException(id);
    }
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return null;
  }

  public void setUpExistingOgel() {
    this.ogel = buildOgel("OGL99");
  }

  public void setUpMissingOgel() {
    this.ogel = null;
  }

  private SpireOgel buildOgel(String id) {
    SpireOgel ogel = new SpireOgel();
    ogel.setId(id);
    ogel.setName("Test OGEL");
    ogel.setLink("www.test.com/ogel");
    return ogel;
  }
}
