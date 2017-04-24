package uk.gov.bis.lite.ogel;

import com.google.inject.AbstractModule;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceMock;

public class GuiceTestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SpireOgelService.class).to(SpireOgelServiceMock.class);
  }
}
