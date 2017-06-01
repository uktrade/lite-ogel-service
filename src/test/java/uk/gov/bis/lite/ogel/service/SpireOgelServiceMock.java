package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.Collections;
import java.util.List;

@Singleton
public class SpireOgelServiceMock implements SpireOgelService {

  private SpireOgel ogel;

  private boolean missingOgel;

  @Inject
  public SpireOgelServiceMock() {
    this.ogel = buildOgel("OGL1");
  }

  @Override
  public List<SpireOgel> getAllOgels() {
    if (missingOgel) {
      return Collections.emptyList();
    }
    return Collections.singletonList(ogel);
  }

  @Override
  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    if (missingOgel) {
      throw new OgelNotFoundException(id);
    } else {
      return ogel;
    }
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return null;
  }

  public void setMissingOgel(boolean missingOgel) {
    this.missingOgel = missingOgel;
  }

  private SpireOgel buildOgel(String id) {
    SpireOgel ogel = new SpireOgel();
    ogel.setId(id);
    ogel.setName("name");
    ogel.setLink("http://example.org");
    return ogel;
  }
}
