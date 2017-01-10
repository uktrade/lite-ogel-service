package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.database.dao.controlcodecondition.LocalControlCodeConditionDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.List;

@Singleton
public class LocalControlCodeConditionService {

  @Inject
  private LocalControlCodeConditionDAO localControlCodeConditionDAO;

  public LocalControlCodeCondition getLocalControlCodeConditionsByIdAndControlCode(String ogelID, String controlCode) {
    return localControlCodeConditionDAO.getLocalControlCodeConditionsByIdAndControlCode(ogelID, controlCode);
  }

  public void insertControlCodeConditionList(List<LocalControlCodeCondition> controlCodeConditionList) {
    for(LocalControlCodeCondition controlCodeCondition : controlCodeConditionList){
      localControlCodeConditionDAO.insertLocalControlCodeCondition(controlCodeCondition);
    }
  }

  public List<LocalControlCodeCondition> getAllControlCodeConditions() {
    return localControlCodeConditionDAO.getAllControlCodeConditions();
  }

  public void deleteControlCodeConditions() {
    localControlCodeConditionDAO.deleteControlCodeConditions();
  }
}
