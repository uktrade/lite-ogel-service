package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.Main;
import uk.gov.bis.lite.ogel.client.SpireOgelClient;
import uk.gov.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundException;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.soap.SOAPMessage;

@Singleton
public class SpireOgelService {
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
    final List<SpireOgel> cacheSpireOgelList = Main.cache.values().stream().collect(Collectors.toList());
    if (!cacheSpireOgelList.isEmpty()) {
      return cacheSpireOgelList;
    } else {
      return getAllOgelsFromSpire();
    }
  }

  public List<SpireOgel> getAllOgelsFromSpire() {
    final SOAPMessage soapMessage = client.executeRequest();
    return unmarshaller.execute(soapMessage);
  }

  public SpireOgel findSpireOgelById(String id) throws OgelNotFoundException {
    if(Main.cache.containsKey(id)){
      return Main.cache.get(id);
    }
    throw new OgelNotFoundException(id);
  }

  public SpireOgel findSpireOgelByIdOrReturnNull(String id) {
    return Main.cache.get(id);
  }
}
