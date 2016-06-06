package uk.gov.bis.lite.ogel.database.dao;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.Main;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundException;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqliteLocalOgelDAOImpl implements LocalOgelDAO {
  private final DBI jdbi;
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  @Inject
  public SqliteLocalOgelDAOImpl(@Named("jdbi") DBI jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public LocalOgel getOgelById(String ogelID) throws LocalOgelNotFoundException {
    LocalOgel ogel = new LocalOgel();
    try (final Handle handle = jdbi.open()) {
      final Map<String, Object> objectMap = handle.createQuery("SELECT ID, NAME FROM LOCAL_OGEL WHERE ID=:id").bind("id", ogelID).first();
      if (objectMap == null) {
        throw new LocalOgelNotFoundException(ogelID);
      }
      ogel.setId(objectMap.get("id").toString());
      //null check for when checking if localOgel exists with just ID
      if (objectMap.get("name") != null) {
        ogel.setName(objectMap.get("name").toString());
      }
      OgelConditionSummary summary = getConditionList(handle, ogelID);
      ogel.setSummary(summary);
      return ogel;
    }
  }

  private OgelConditionSummary getConditionList(Handle handler, String ogelID) {
    final List<Map<String, Object>> list = handler.createQuery("SELECT * FROM CONDITION_LIST WHERE OGELID=:id ORDER BY ROWID")
        .bind("id", ogelID).list();
    OgelConditionSummary summary = new OgelConditionSummary();
    summary.setCanList(getSpecificConditionList("canList", list));
    summary.setCantList(getSpecificConditionList("cantList", list));
    summary.setMustList(getSpecificConditionList("mustList", list));
    summary.setHowToUseList(getSpecificConditionList("howToUseList", list));
    return summary;
  }

  private List<String> getSpecificConditionList(String conditionType, List<Map<String, Object>> list) {
    return list.stream().filter(c -> c.get("type").toString().equalsIgnoreCase(conditionType))
        .map(cond -> cond.get("condition").toString()).collect(Collectors.toList());
  }

  @Override
  @Transaction
  public LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception {
    try (final Handle handle = jdbi.open()) {
      handle.execute("DELETE FROM CONDITION_LIST WHERE OGELID = ? AND TYPE = ?", ogelID, fieldName);
      updateData.stream().forEach(u -> insertConditionListForOgel(handle, ogelID, fieldName, u));
    }
    return getOgelById(ogelID);
  }

  @Override
  @Transaction
  public LocalOgel insertLocalOgel(LocalOgel localOgel) {
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
    return localOgel;
  }

  @Override
  @Transaction
  public void deleteOgel(String ogelID) {
    try (final Handle handle = jdbi.open()) {
      handle.execute("DELETE FROM LOCAL_OGEL WHERE ID = ?", ogelID);
      handle.execute("DELETE FROM CONDITION_LIST WHERE OGELID = ? ", ogelID);
    }
  }

  @Override
  @Transaction
  public LocalOgel insertOrUpdate(LocalOgel newOgel) {
    try {
      getOgelById(newOgel.getId());
    } catch (LocalOgelNotFoundException e) {
      return insertLocalOgel(newOgel);
    }
    deleteOgel(newOgel.getId());
    return insertLocalOgel(newOgel);
  }

  private void insertConditionListForOgel(Handle handle, String id, String type, String condition) {
    handle.execute("INSERT INTO CONDITION_LIST(OGELID, TYPE, CONDITION) VALUES (?, ?, ?)", id, type, condition);
  }
}
