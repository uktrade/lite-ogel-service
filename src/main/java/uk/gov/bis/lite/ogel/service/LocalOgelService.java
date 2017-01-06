package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.database.dao.ogel.LocalOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.validator.CheckLocalOgel;

import java.util.List;

@Singleton
public class LocalOgelService {

  @Inject
  private LocalOgelDAO localOgelDAO;

  public LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) {
    return localOgelDAO.updateSingleOgelConditionList(ogelID, newConditionList, conditionField);
  }

  public LocalOgel findLocalOgelById(String id) {
    return localOgelDAO.getOgelById(id);
  }

  public LocalOgel insertOrUpdateOgel(@CheckLocalOgel LocalOgel ogel) {
    return localOgelDAO.insertOrUpdate(ogel);
  }

  public void insertOgelList(List<LocalOgel> ogelList) {
    for(LocalOgel lo : ogelList){
      localOgelDAO.insertOrUpdate(lo);
    }
  }

  public List<LocalOgel> getAllLocalOgels() {
    return localOgelDAO.getAllOgels();
  }

}
