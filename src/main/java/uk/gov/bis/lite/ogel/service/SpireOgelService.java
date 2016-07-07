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

  @Inject
  public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
    this.client = client;
    this.unmarshaller = unmarshaller;
  }

  public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
    return SpireOgelFilter.filterSpireOgels(getAllOgels(), controlCode, destinationCountryId, activityTypes);
  }

  public List<SpireOgel> getAllOgels() {
    final List<SpireOgel> cacheSpireOgelList = new ArrayList<>(cache.values());
    if (!cacheSpireOgelList.isEmpty()) {
      return cacheSpireOgelList;
    } else {
      throw new CacheNotPopulatedException("Communication with Spire failed. Spire Ogel list is not populated");
    }
  }

  private List<SpireOgel> getAllOgelsFromSpire() {
    final SOAPMessage soapMessage = client.executeRequest();
    return unmarshaller.execute(soapMessage);
  }

  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    if (cache.containsKey(id)) {
      return cache.get(id);
    }
    throw new OgelNotFoundException(id);
  }

  public SpireOgel findSpireOgelByIdOrReturnNull(String id) {
    return cache.get(id);
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
      List<SpireOgel> ogelList = spireOgelService.getAllOgelsFromSpire();
      ogelList.forEach(o -> spireOgelCacheMap.put(o.getId(), o));
      if (spireOgelCacheMap.size() > 0) {
        cache = Collections.unmodifiableMap(spireOgelCacheMap);
        LOGGER.info("Cache has been successfully updated");
      } else {
        LOGGER.warn("Cache refresh job could not retrieve new data from Spire.");
      }
    }
  }
}
