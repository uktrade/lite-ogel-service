package uk.gov.bis.lite.ogel.database.dao.controlcodecondition;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalControlCodeCondition;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class LocalControlCodeConditionRSMapper implements ResultSetMapper<LocalControlCodeCondition> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalControlCodeConditionRSMapper.class);

  @Override
  public LocalControlCodeCondition map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    LocalControlCodeCondition localOgel = new LocalControlCodeCondition();
    localOgel.setOgelID(r.getString("OGEL_ID"));
    localOgel.setControlCode(r.getString("CONTROL_CODE"));
    localOgel.setConditionDescription(r.getString("CONDITION_DESC"));

    try {
      String conditionDescControlCodes = r.getString("CONDITION_DESC_CONTROL_CODES");
      List<String> conditionControlCodeList = Collections.emptyList();
      ObjectMapper mapper = new ObjectMapper();
      if (conditionDescControlCodes != null) {
        conditionControlCodeList = mapper.readValue(conditionDescControlCodes,
            mapper.getTypeFactory().constructCollectionType(List.class, String.class));
      }
      localOgel.setConditionDescriptionControlCodes(conditionControlCodeList);
    } catch (IOException e) {
      LOGGER.error("An error occurred parsing the Local OGEL Condition Description Control Codes Json", e);
      throw new RuntimeException("An error occurred parsing the Local OGEL Condition Description Control Codes Json", e);
    }

    localOgel.setItemsAllowed(r.getBoolean("ALLOWED"));

    return localOgel;
  }
}
