package uk.gov.bis.lite.ogel.database.dao.ogel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.commons.collections4.ListUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgelConditionSummary;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class LocalOgelRSMapper implements ResultSetMapper<LocalOgel> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalOgelRSMapper.class);

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final CollectionType STRING_LIST_COLLECTION_TYPE = MAPPER.getTypeFactory()
      .constructCollectionType(List.class, String.class);

  @Override
  public LocalOgel map(int index, ResultSet r, StatementContext ctx) throws SQLException {

    LocalOgel localOgel = new LocalOgel();
    localOgel.setId(r.getString("id"));
    localOgel.setName(r.getString("name"));
    LocalOgelConditionSummary summary = new LocalOgelConditionSummary();
    try {
      summary.setCanList(getConditionList("canList", r));
      summary.setCantList(getConditionList("cantList", r));
      summary.setMustList(getConditionList("mustList", r));
      summary.setHowToUseList(getConditionList("howToUseList", r));
    } catch (IOException e) {
      LOGGER.error("An error occurred parsing the Local Ogel Json", e);
      throw new RuntimeException(e.getCause());
    }
    localOgel.setSummary(summary);
    return localOgel;
  }

  private List<String> getConditionList(String conditionType, ResultSet r) throws SQLException, IOException {
    String json = r.getString(conditionType);
    if (json == null) {
      return Collections.emptyList();
    } else {
      List<String> conditionList = MAPPER.readValue(r.getString(conditionType), STRING_LIST_COLLECTION_TYPE);
      return ListUtils.emptyIfNull(conditionList);
    }
  }
}
