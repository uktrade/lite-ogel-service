package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.Collections;
import java.util.List;

@Singleton
public class SpireOgelServiceMock implements SpireOgelService {

  private final String virtualEuOgelId;
  private boolean missingOgel;
  private boolean isVirtualEu;

  @Inject
  public SpireOgelServiceMock(@Named("virtualEuOgelId") String virtualEuOgelId) {
    this.virtualEuOgelId = virtualEuOgelId;
  }

  @Override
  public List<SpireOgel> getAllOgels() {
    if (missingOgel) {
      return Collections.emptyList();
    }
    return Collections.singletonList(buildOgel());
  }

  @Override
  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    if (missingOgel) {
      throw new OgelNotFoundException(id);
    } else {
      return buildOgel();
    }
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return null;
  }

  public SpireOgelServiceMock setMissingOgel(boolean missingOgel) {
    this.missingOgel = missingOgel;
    return this;
  }

  public SpireOgelServiceMock setVirtualEu(boolean virtualEu) {
    isVirtualEu = virtualEu;
    return this;
  }

  private SpireOgel buildOgel() {
    SpireOgel ogel = new SpireOgel();
    if (isVirtualEu) {
      ogel.setId(virtualEuOgelId);
    }
    else {
      ogel.setId("OGL1");
    }
    ogel.setName("name");
    ogel.setLink("http://example.org");
    return ogel;
  }
}
