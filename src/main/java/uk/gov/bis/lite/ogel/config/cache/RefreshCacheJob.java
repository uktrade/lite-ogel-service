package uk.gov.bis.lite.ogel.config.cache;

import com.fiestacabin.dropwizard.quartz.Scheduled;
import com.google.inject.Inject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.Main;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Scheduled(interval = 1, unit = TimeUnit.HOURS)
public class RefreshCacheJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshCacheJob.class);

  @Inject
  private SpireOgelService spireOgelService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    HashMap<String, SpireOgel> spireOgelCacheMap = new HashMap<>();
    List<SpireOgel> ogelList = spireOgelService.getAllOgelsFromSpire();
    ogelList.forEach(o -> spireOgelCacheMap.put(o.getId(), o));
    if (spireOgelCacheMap.size() > 0) {
      Main.cache = Collections.unmodifiableMap(spireOgelCacheMap);
      LOGGER.info("Cache has been successfully updated");
    }
  }
}
