package uk.gov.bis.lite.ogel.database.dao;

import static uk.gov.bis.lite.ogel.Main.jdbi;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SqliteLocalOgelDAOImpl implements LocalOgelDAO {

  @Override
  public Optional<LocalOgel> getOgelById(String ogelID) {
    LocalOgel ogel = new LocalOgel();
    try (final Handle handle = jdbi.open()) {
      final Map<String, Object> objectMap = handle.createQuery("SELECT ID, NAME FROM LOCAL_OGEL WHERE ID=:id").bind("id", ogelID).first();
      if (objectMap.get("id") == null) {
        return Optional.empty();
      }
      ogel.setId(objectMap.get("id").toString());
      ogel.setName(objectMap.get("name").toString());
      OgelSummary summary = new OgelSummary();
      summary.setCanList(getConditionList(handle, ogelID, "canList"));
      summary.setCantList(getConditionList(handle, ogelID, "cantList"));
      summary.setMustList(getConditionList(handle, ogelID, "mustList"));
      summary.setHowToUseList(getConditionList(handle, ogelID, "howToUseList"));
      ogel.setSummary(summary);
      return Optional.of(ogel);
    }
  }

  private List<String> getConditionList(Handle handler, String ogelID, String type) {
    final List<Map<String, Object>> list = handler.createQuery("SELECT * FROM CONDITION_LIST WHERE OGELID=:id AND TYPE=:type")
        .bind("id", ogelID).bind("type", type).list();
    return list.stream().map(cond -> cond.get("condition").toString()).collect(Collectors.toList());
  }

  @Override
  public LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception {
    try (final Handle handle = jdbi.open()) {

      handle.getConnection().setAutoCommit(false);
      handle.execute("DELETE FROM CONDITION_LIST WHERE OGELID = ? AND TYPE = ?", ogelID, fieldName);
      updateData.stream().forEach(u -> insertConditionListForOgel(handle, ogelID, fieldName, u));
      handle.getConnection().commit();
    }
    return getOgelById(ogelID).get();
  }

  @Override
  public void insertLocalOgel(LocalOgel localOgel) {
    try (final Handle handle = jdbi.open()) {
      handle.execute("INSERT INTO LOCAL_OGEL(ID, NAME) VALUES (?, ?)", localOgel.getId(), localOgel.getName());
      localOgel.getSummary().getCanList().stream().forEach(condition -> {
        insertConditionListForOgel(handle, localOgel.getId(), "canList", condition);
      });
      localOgel.getSummary().getCantList().stream().forEach(condition -> {
        insertConditionListForOgel(handle, localOgel.getId(), "cantList", condition);
      });
      localOgel.getSummary().getMustList().stream().forEach(condition -> {
        insertConditionListForOgel(handle, localOgel.getId(), "mustList", condition);
      });
      localOgel.getSummary().getHowToUseList().stream().forEach(condition -> {
        insertConditionListForOgel(handle, localOgel.getId(), "howToUseList", condition);
      });
    }
  }

  @Override
  @SqlUpdate("CREATE TABLE IF NOT EXISTS LOCAL_OGEL(ID TEXT, NAME TEXT)")
  public void createLocalOgelTable() {
  }

  @Override
  @SqlUpdate("CREATE TABLE IF NOT EXISTS CONDITION_LIST(OGELID TEXT, TYPE TEXT, CONDITION TEXT)")
  public void createConditionListTable() {
  }

  private void insertConditionListForOgel(Handle handle, String id, String type, String condition) {
    handle.execute("INSERT INTO CONDITION_LIST(OGELID, TYPE, CONDITION) VALUES (?, ?, ?)", id, type, condition);
  }
}
