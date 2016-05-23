package uk.gov.bis.lite.ogel.database.dao;

import static uk.gov.bis.lite.ogel.Main.DB_URL;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteLocalOgelDAOImpl implements LocalOgelDAO {

  @Override
  public List<LocalOgel> getAllLocalOgels() {
    List<LocalOgel> ogels = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(DB_URL);
         PreparedStatement selectOgelSt = connection.prepareStatement("SELECT ID, NAME FROM LOCAL_OGEL");
         PreparedStatement selectCanConditionSt = connection.prepareStatement("SELECT * FROM CAN_LIST WHERE OGELID=?");
         PreparedStatement selectCantConditionSt = connection.prepareStatement("SELECT * FROM CANT_LIST WHERE OGELID=?");
         PreparedStatement selectMustConditionSt = connection.prepareStatement("SELECT * FROM MUST_LIST WHERE OGELID=?");
         PreparedStatement selectHowToConditionSt = connection.prepareStatement("SELECT * FROM HOW_TO_USE_LIST WHERE OGELID=?");
         ResultSet rs = selectOgelSt.executeQuery();
    ) {

      LocalOgel ogel;
      while (rs.next()) {
        ogel = new LocalOgel();
        ogel.setId(rs.getString("ID"));
        ogel.setName(rs.getString("NAME"));

        OgelSummary summary = new OgelSummary();
        summary.setCanList(getConditions(selectCanConditionSt, ogel.getId()));
        summary.setCantList(getConditions(selectCantConditionSt, ogel.getId()));
        summary.setMustList(getConditions(selectMustConditionSt, ogel.getId()));
        summary.setHowToUseList(getConditions(selectHowToConditionSt, ogel.getId()));

        ogel.setSummary(summary);
        ogels.add(ogel);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ogels;
  }

  private List<String> getConditions(PreparedStatement st, String ogelId) throws SQLException {
    st.setString(1, ogelId);
    final ResultSet resultSet = st.executeQuery();
    List<String> conditionList = new ArrayList<>();
    while (resultSet.next()) {
      final String condition = resultSet.getString(2);
      conditionList.add(condition);
    }
    return conditionList;
  }

  @Override
  public LocalOgel getOgelById(String ogelID) {
    return null;
  }

  @Override
  public LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception {
    return null;
  }

  @Override
  public void insertLocalOgel(LocalOgel localOgel) {
    try (
        Connection connection = DriverManager.getConnection(DB_URL);

        PreparedStatement statement = connection.prepareStatement("INSERT INTO LOCAL_OGEL(ID, NAME) VALUES (?, ?)");
        PreparedStatement canListStatement = connection.prepareStatement("INSERT INTO CAN_LIST(OGELID, CONDITION) VALUES (?, ?)");
        PreparedStatement cantListStatement = connection.prepareStatement("INSERT INTO CANT_LIST(OGELID, CONDITION) VALUES (?, ?)");
        PreparedStatement mustListStatement = connection.prepareStatement("INSERT INTO MUST_LIST(OGELID, CONDITION) VALUES (?, ?)");
        PreparedStatement howToDoListStatement = connection.prepareStatement("INSERT INTO HOW_TO_USE_LIST(OGELID, CONDITION) VALUES (?, ?)");
    ) {
      statement.setString(1, localOgel.getId());
      statement.setString(2, localOgel.getName());
      statement.executeUpdate();
      localOgel.getSummary().getCanList().stream().forEach(condition -> {
        insertConditionListForOgel(canListStatement, localOgel.getId(), condition);
      });
      localOgel.getSummary().getCantList().stream().forEach(condition -> {
        insertConditionListForOgel(cantListStatement, localOgel.getId(), condition);
      });
      localOgel.getSummary().getMustList().stream().forEach(condition -> {
        insertConditionListForOgel(mustListStatement, localOgel.getId(), condition);
      });
      localOgel.getSummary().getHowToUseList().stream().forEach(condition -> {
        insertConditionListForOgel(howToDoListStatement, localOgel.getId(), condition);
      });
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createDatabase() {
    try (
        Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement localOgelTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS LOCAL_OGEL(ID TEXT, NAME TEXT)");
        PreparedStatement canListTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS CAN_LIST(OGELID TEXT, CONDITION TEXT)");
        PreparedStatement cantListTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS CANT_LIST(OGELID TEXT, CONDITION TEXT)");
        PreparedStatement mustListTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS MUST_LIST(OGELID TEXT, CONDITION TEXT)");
        PreparedStatement howToUseListTableStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS HOW_TO_USE_LIST(OGELID TEXT, CONDITION TEXT)");
    ) {
      localOgelTableStatement.execute();
      canListTableStatement.execute();
      cantListTableStatement.execute();
      mustListTableStatement.execute();
      howToUseListTableStatement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void insertConditionListForOgel(PreparedStatement ps, String id, String condition) {
    try {
      ps.setString(1, id);
      ps.setString(2, condition);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
