package uk.gov.bis.lite.ogel;

import com.google.inject.AbstractModule;
import uk.gov.bis.lite.ogel.service.ApplicableOgelService;
import uk.gov.bis.lite.ogel.service.ApplicableOgelServiceMock;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.LocalOgelServiceMock;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceMock;

public class GuiceTestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(LocalOgelService.class).to(LocalOgelServiceMock.class);
    bind(SpireOgelService.class).to(SpireOgelServiceMock.class);
    bind(ApplicableOgelService.class).to(ApplicableOgelServiceMock.class);
  }
}
