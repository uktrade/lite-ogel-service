package uk.gov.bis.lite.ogel.database.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LocalOgelRSMapper implements ResultSetMapper<LocalOgel> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalOgelRSMapper.class);

  @Override
  public LocalOgel map(int index, ResultSet r, StatementContext ctx) throws SQLException {

    LocalOgel localOgel = new LocalOgel();
    localOgel.setId(r.getString("id"));
    localOgel.setName(r.getString("name"));
    OgelConditionSummary summary = new OgelConditionSummary();
    try {
      summary.setCanList(getConditionList("canList", r));
      summary.setCantList(getConditionList("cantList", r));
      summary.setMustList(getConditionList("mustList", r));
      summary.setHowToUseList(getConditionList("howToUseList", r));
    } catch (IOException e) {
      LOGGER.error("An error occurred parsing the Local Ogel Json", e);
    }
    localOgel.setSummary(summary);
    return localOgel;
  }

  private List<String> getConditionList(String conditionType, ResultSet r) throws SQLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    if (r.getString(conditionType) != null) {
      return mapper.readValue(r.getString(conditionType),
          mapper.getTypeFactory().constructCollectionType(List.class, String.class));
    }
    return null;
  }
}
