package uk.gov.bis.lite.ogel;

import com.fiestacabin.dropwizard.quartz.SchedulerConfiguration;
import uk.gov.bis.lite.ogel.config.guice.GuiceModule;
import uk.gov.bis.lite.ogel.database.dao.controlcodecondition.LocalControlCodeConditionDAO;
import uk.gov.bis.lite.ogel.database.dao.controlcodecondition.SqliteLocalControlCodeConditionDAOImpl;
import uk.gov.bis.lite.ogel.database.dao.ogel.LocalOgelDAO;
import uk.gov.bis.lite.ogel.database.dao.ogel.SqliteLocalOgelDAOImpl;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelServiceMock;

public class GuiceTestModule extends GuiceModule {

  @Override
  protected void configure() {
    bind(SchedulerConfiguration.class).toInstance(new SchedulerConfiguration("uk.gov.bis.lite.ogel"));
    bind(LocalOgelDAO.class).to(SqliteLocalOgelDAOImpl.class);
    bind(LocalControlCodeConditionDAO.class).to(SqliteLocalControlCodeConditionDAOImpl.class);
    bind(SpireOgelService.class).to(SpireOgelServiceMock.class);
  }
}
