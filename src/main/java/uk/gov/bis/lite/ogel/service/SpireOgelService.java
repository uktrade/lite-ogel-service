package uk.gov.bis.lite.ogel.service;

import static uk.gov.bis.lite.ogel.Main.CACHE_KEY;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.log4j.Logger;
import uk.gov.bis.lite.ogel.Main;
import uk.gov.bis.lite.ogel.client.SpireOgelClient;
import uk.gov.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

@Singleton
public class SpireOgelService {
  private SpireOgelClient client;
  private SpireOgelSOAPUnmarshaller unmarshaller;

  final static Logger logger = Logger.getLogger(SpireOgelService.class);

  @Inject
  private CacheManager cacheManager;

  @Inject
  public SpireOgelService(SpireOgelClient client, SpireOgelSOAPUnmarshaller unmarshaller) {
    this.client = client;
    this.unmarshaller = unmarshaller;
  }

  public List<SpireOgel> findOgel(String controlCode, String destinationCountryId, List<CategoryType> activityTypes) {
    Ehcache cache = cacheManager.getEhcache(Main.CACHE_NAME);
    if (cache.get(CACHE_KEY) != null) {
      List<SpireOgel> ogelsList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
      return SpireOgelFilter.filterSpireOgels(ogelsList, controlCode, destinationCountryId, activityTypes);
    }
    return null;
  }

  public List<SpireOgel> getAllOgels() throws XPathExpressionException, SOAPException, UnsupportedEncodingException {
    Ehcache cache = cacheManager.getEhcache(Main.CACHE_NAME);
    final List<SpireOgel> cacheSpireOgelList = (List<SpireOgel>) cache.get(CACHE_KEY).getObjectValue();
    if (!cacheSpireOgelList.isEmpty()) {
      return cacheSpireOgelList;
    } else {
      return initializeCache();
    }
  }

  public List<SpireOgel> initializeCache() throws XPathExpressionException, SOAPException, UnsupportedEncodingException {
    final SOAPMessage soapMessage = client.executeRequest();
    return unmarshaller.execute(soapMessage);

  }
}
