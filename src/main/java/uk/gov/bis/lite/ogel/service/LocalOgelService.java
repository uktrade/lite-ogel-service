package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

public class LocalOgelService {

  @Inject
  private LocalOgelDAO localOgelDAO;

  public LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) throws Exception {
    return localOgelDAO.updateOgelConditionList(ogelID, newConditionList, conditionField);
  }

  public LocalOgel findLocalOgelById(String id) {
    return localOgelDAO.getOgelById(id);
  }
}
