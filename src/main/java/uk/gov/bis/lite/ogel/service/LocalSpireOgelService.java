package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.database.dao.LocalSpireOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;

import java.util.List;

@Singleton
public class LocalSpireOgelService {

  @Inject
  private LocalSpireOgelDAO localSpireOgelDAO;

  public List<?> getAllLocalOgels() {
    return localSpireOgelDAO.getAllLocalSpireOgels();
  }

  public LocalSpireOgel getSpireOgelByID(String id) {
    return localSpireOgelDAO.getSpireOgelById(id);
  }

  public LocalSpireOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) {
    return localSpireOgelDAO.updateSpireOgelCanList(ogelID, newConditionList, conditionField);
  }
}
