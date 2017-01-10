package uk.gov.bis.lite.ogel.database.dao.controlcodecondition;

import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.List;

public interface LocalControlCodeConditionDAO {

  LocalControlCodeCondition getLocalControlCodeConditionsByIdAndControlCode(String ogelID, String controlCode);

  LocalControlCodeCondition insertLocalControlCodeCondition(LocalControlCodeCondition controlCodeCondition);

  List<LocalControlCodeCondition> getAllControlCodeConditions();

  void deleteControlCodeConditions();
}
