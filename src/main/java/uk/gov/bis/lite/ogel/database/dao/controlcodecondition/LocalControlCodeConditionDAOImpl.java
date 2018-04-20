package uk.gov.bis.lite.ogel.database.dao.controlcodecondition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalControlCodeCondition;

import java.util.List;

public class LocalControlCodeConditionDAOImpl implements LocalControlCodeConditionDAO {
  private final DBI jdbi;

  @Inject
  public LocalControlCodeConditionDAOImpl(@Named("jdbi") DBI jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<LocalControlCodeCondition> getAllControlCodeConditions() {
    try (final Handle handle = jdbi.open()) {
      LocalControlCodeConditionJDBIDao jdbiDao = handle.attach(LocalControlCodeConditionJDBIDao.class);
      return jdbiDao.getAllLocalControlCodeConditions();
    }
  }

  @Override
  public LocalControlCodeCondition getLocalControlCodeConditionsByIdAndControlCode(String ogelID, String controlCode) {
    try (final Handle handle = jdbi.open()) {
      LocalControlCodeConditionJDBIDao jdbiDao = handle.attach(LocalControlCodeConditionJDBIDao.class);
      return jdbiDao.getLocalControlCodeConditionsByIdAndControlCode(ogelID, controlCode);
    }
  }

  @Override
  public LocalControlCodeCondition insertLocalControlCodeCondition(LocalControlCodeCondition localControlCodeCondition) {
    try (final Handle handle = jdbi.open()) {
      insertLocalControlCodeCondition(localControlCodeCondition, handle);
      return getLocalControlCodeConditionsByIdAndControlCode(localControlCodeCondition.getOgelID(), localControlCodeCondition.getControlCode());
    }
  }

  @Override
  public void deleteControlCodeConditions() {
    try (final Handle handle = jdbi.open()) {
      LocalControlCodeConditionJDBIDao jdbiDao = handle.attach(LocalControlCodeConditionJDBIDao.class);
      jdbiDao.deleteControlCodeConditions();
    }
  }

  private void insertLocalControlCodeCondition(LocalControlCodeCondition localControlCodeCondition, Handle handle) {
    LocalControlCodeConditionJDBIDao jdbiDao = handle.attach(LocalControlCodeConditionJDBIDao.class);
    jdbiDao.insertNewLocalControlCodeCondition(localControlCodeCondition.getOgelID(), localControlCodeCondition.getControlCode(),
        localControlCodeCondition.getConditionDescription(),
        parseListToJson(localControlCodeCondition.getConditionDescriptionControlCodes()),
        localControlCodeCondition.isItemsAllowed());

  }

  private String parseListToJson(List<String> conditionList) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(conditionList);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
