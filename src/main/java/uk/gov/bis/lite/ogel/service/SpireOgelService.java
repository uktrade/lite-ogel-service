package uk.gov.bis.lite.ogel.service;

import com.fiestacabin.dropwizard.quartz.Scheduled;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.client.SpireOgelClient;
import uk.gov.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.job.SpireHealthStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.soap.SOAPMessage;

@Singleton
public class SpireOgelService {
  private static Map<String, SpireOgel> cache = new HashMap<>();
  private SpireOgelClient client;
  private SpireOgelSOAPUnmarshaller unmarshaller;
  private static SpireHealthStatus healthStatus = SpireHealthStatus.unhealthy("Service not initialised");

  @Inject
  public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
    this.client = client;
    this.unmarshaller = unmarshaller;
  }

  public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
    if (cache.isEmpty()) {
      throw new CacheNotPopulatedException("Communication with Spire failed. Spire Ogel list is not populated");
    }
    return SpireOgelFilter.filterSpireOgels(getAllOgels(), controlCode, destinationCountryId, activityTypes);
  }

  public List<SpireOgel> getAllOgels() {
    if (cache.isEmpty()) {
      throw new CacheNotPopulatedException("Communication with Spire failed. Spire Ogel list is not populated");
    }
    return new ArrayList<>(cache.values());
  }

  private List<SpireOgel> getAllOgelsFromSpire() {
    final SOAPMessage soapMessage = client.executeRequest();
    return unmarshaller.execute(soapMessage);
  }

  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    if (cache.isEmpty()) {
      throw new CacheNotPopulatedException("Communication with Spire failed. Spire Ogel list is not populated");
    }
    if (cache.containsKey(id)) {
      return cache.get(id);
    }
    throw new OgelNotFoundException(id);
  }

  public static SpireHealthStatus getHealthStatus() {
    return healthStatus;
  }

  @DisallowConcurrentExecution
  @PersistJobDataAfterExecution
  @Scheduled(interval = 1, unit = TimeUnit.HOURS)
  private static class RefreshCacheJob implements Job {
    private final Logger LOGGER = LoggerFactory.getLogger(RefreshCacheJob.class);

    @Inject
    private SpireOgelService spireOgelService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      HashMap<String, SpireOgel> spireOgelCacheMap = new HashMap<>();
      try {
        List<SpireOgel> ogelList = spireOgelService.getAllOgelsFromSpire();
        ogelList.forEach(o -> spireOgelCacheMap.put(o.getId(), o));
        if (spireOgelCacheMap.size() > 0) {
          cache = Collections.unmodifiableMap(spireOgelCacheMap);
          healthStatus = SpireHealthStatus.healthy();
          LOGGER.info("Cache has been successfully updated at {}", healthStatus.getLastUpdated());
        } else {
          healthStatus = SpireHealthStatus.unhealthy("Cache size is 0");
          LOGGER.warn("Cache refresh job failed to retrieve new data from Spire.");
        }
      } catch (Exception e) {
        LOGGER.warn("An unexpected error occurred getting the Ogel Data from Spire", e);
        healthStatus = SpireHealthStatus.unhealthy(e.getMessage());
      }
    }
  }
}
