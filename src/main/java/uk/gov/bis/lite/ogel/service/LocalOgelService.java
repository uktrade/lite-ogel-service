package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.sql.SQLException;
import java.util.List;

@Singleton
public class LocalOgelService {

  @Inject
  private LocalOgelDAO localOgelDAO;

  public LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) {
    return localOgelDAO.updateOgelConditionList(ogelID, newConditionList, conditionField);
  }

  public LocalOgel findLocalOgelById(String id) throws LocalOgelNotFoundException {
    return localOgelDAO.getOgelById(id);
  }

  public LocalOgel insertOrUpdateOgel(LocalOgel ogel) {
    return localOgelDAO.insertOrUpdate(ogel);
  }

  public void insertOgelList(List<LocalOgel> ogelList) throws SQLException {
    localOgelDAO.insertLocalOgels(ogelList);
  }

  public List<LocalOgel> getAllLocalOgels() {
    return localOgelDAO.getAllOgels();
  }
}
