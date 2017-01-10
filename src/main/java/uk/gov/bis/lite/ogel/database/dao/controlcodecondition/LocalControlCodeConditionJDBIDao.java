package uk.gov.bis.lite.ogel.database.dao.controlcodecondition;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.List;

public interface LocalControlCodeConditionJDBIDao {
  @SqlQuery("SELECT OGEL_ID, CONTROL_CODE, CONDITION_DESC, CONDITION_DESC_CONTROL_CODES, ALLOWED " +
      "FROM LOCAL_CONTROL_CODE_CONDITIONS " +
      "WHERE OGEL_ID = :ogelID " +
      "AND CONTROL_CODE = :controlCode")
  @Mapper(LocalControlCodeConditionRSMapper.class)
  LocalControlCodeCondition getLocalControlCodeConditionsByIdAndControlCode(@Bind("ogelID") String ogelID, @Bind("controlCode") String controlCode);

  @SqlQuery("SELECT OGEL_ID, CONTROL_CODE, CONDITION_DESC, CONDITION_DESC_CONTROL_CODES, ALLOWED FROM LOCAL_CONTROL_CODE_CONDITIONS")
  @Mapper(LocalControlCodeConditionRSMapper.class)
  List<LocalControlCodeCondition> getAllLocalControlCodeConditions();


  @SqlUpdate("INSERT INTO LOCAL_CONTROL_CODE_CONDITIONS (OGEL_ID, CONTROL_CODE, CONDITION_DESC, CONDITION_DESC_CONTROL_CODES, ALLOWED) " +
      "VALUES (:ogelID, :controlCode, :conditionDesc, :conditionDescControlCodes, :Allowed)")
  void insertNewLocalControlCodeCondition(@Bind("ogelID") String ogelID, @Bind("controlCode") String controlCode,
                                          @Bind("conditionDesc") String conditionDesc,
                                          @Bind("conditionDescControlCodes") String conditionDescControlCodes,
                                          @Bind("Allowed") Boolean allowed);

  @SqlUpdate("DELETE FROM LOCAL_CONTROL_CODE_CONDITIONS")
  void deleteControlCodeConditions();
}
