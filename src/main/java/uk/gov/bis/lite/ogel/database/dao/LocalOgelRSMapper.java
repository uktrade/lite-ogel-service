package uk.gov.bis.lite.ogel.database.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LocalOgelRSMapper implements ResultSetMapper<LocalOgel> {
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
      e.printStackTrace();
    }
    localOgel.setSummary(summary);
    return localOgel;
  }

  private List<String> getConditionList(String conditionType, ResultSet r) throws SQLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(r.getString(conditionType),
        mapper.getTypeFactory().constructCollectionType(List.class, String.class));
  }
}
