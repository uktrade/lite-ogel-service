package uk.gov.bis.lite.ogel.service;

import static uk.gov.bis.lite.ogel.Main.CACHE_KEY;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import uk.gov.bis.lite.ogel.Main;
import uk.gov.bis.lite.ogel.client.SpireOgelClient;
import uk.gov.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;

import javax.xml.soap.SOAPMessage;

@Singleton
public class SpireOgelService {
  private SpireOgelClient client;
  private SpireOgelSOAPUnmarshaller unmarshaller;

  @Inject
  private CacheManager cacheManager;

  @Inject
  public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
    this.client = client;
    this.unmarshaller = unmarshaller;
  }

  public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
    final Ehcache cache = cacheManager.getEhcache(Main.CACHE_NAME);
    if (cache.get(CACHE_KEY) != null) {
      List<SpireOgel> ogelsList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
      return SpireOgelFilter.filterSpireOgels(ogelsList, controlCode, destinationCountryId, activityTypes);
    }
    return null;
  }

  public List<SpireOgel> getAllOgels() {
    final Ehcache cache = cacheManager.getEhcache(Main.CACHE_NAME);
    final List<SpireOgel> cacheSpireOgelList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
    if (!cacheSpireOgelList.isEmpty()) {
      return cacheSpireOgelList;
    } else {
      return initializeCache();
    }
  }

  public List<SpireOgel> initializeCache()  {
    final SOAPMessage soapMessage = client.executeRequest();
    return unmarshaller.execute(soapMessage);
  }

  public SpireOgel findSpireOgelById(List<SpireOgel> ogelList, String id) throws OgelNotFoundException{
    return ogelList.stream().filter(ogel -> ogel.getId().equalsIgnoreCase(id)).findFirst().orElseThrow(() -> new OgelNotFoundException(id));
  }
}
