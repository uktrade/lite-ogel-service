package uk.gov.bis.lite.ogel.database.dao;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.database.ListJsonMapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgelLookUp;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Singleton
public class LocalOgelFlatFileImpl implements LocalOgelDAO {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalOgelFlatFileImpl.class);
  private static final String LOCAL_OGEL_CONDITION_DATA_FILE = "ogel-condition-data.json";
  private static final String LOCAL_OGEL_LOOKUP_DATA_FILE = "ogel-lookup-data.json";

  private static List<LocalOgel> localOgels;

  //@Override
  public List<LocalOgel> getAllLocalOgels() {
    if (localOgels == null) {
      try {
        LOGGER.info("Storing the values retrieved from {}", LOCAL_OGEL_CONDITION_DATA_FILE);
        List<LocalOgel> localOgelList = (List<LocalOgel>)
            new ListJsonMapper().mapToListFromJson(LOCAL_OGEL_CONDITION_DATA_FILE, LocalOgel.class);

        LOGGER.info("Storing the values retrieved from {}", LOCAL_OGEL_LOOKUP_DATA_FILE);
        List<LocalOgelLookUp> localOgelLookUpList = (List<LocalOgelLookUp>)
            new ListJsonMapper().mapToListFromJson(LOCAL_OGEL_LOOKUP_DATA_FILE, LocalOgelLookUp.class);

        for (LocalOgel localOgel : localOgelList) {
          localOgelLookUpList.stream().filter(localLookUp -> localOgel.getName().equalsIgnoreCase(localLookUp.getName()))
              .forEach(localLookUp -> localOgel.setId(localLookUp.getId()));
        }
        localOgels = localOgelList;
        return localOgelList;
      } catch (IOException e) {
        LOGGER.warn("An error occurred trying to populate the database", e);
      }
    }
    return localOgels;
  }

  @Override
  public Optional<LocalOgel> getOgelById(String ogelID) {
    if (localOgels == null) {
      getAllLocalOgels();
    }
    return localOgels.stream().filter(o -> o.getId().equalsIgnoreCase(ogelID)).findFirst();
  }

  @Override
  public LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception {
    final Optional<LocalOgel> foundOgelCondition = getOgelById(ogelID);
    switch (fieldName) {
      case "canList":
        foundOgelCondition.get().getSummary().setCanList(updateData);
        break;
      case "cantList":
        foundOgelCondition.get().getSummary().setCantList(updateData);
        break;
      case "mustList":
        foundOgelCondition.get().getSummary().setMustList(updateData);
        break;
      case "howToUseList":
        foundOgelCondition.get().getSummary().setHowToUseList(updateData);
        break;
      default:
        LOGGER.error("Update operation unsuccessful. Invalid condition field name " + fieldName);
        throw new RuntimeException("Invalid local ogel condition parameter " + fieldName);
    }
    return foundOgelCondition.get();
  }

  @Override
  public void insertLocalOgel(LocalOgel localOgel) {
  }

  @Override
  public void createLocalOgelTable() {

  }

  @Override
  public void createConditionListTable() {

  }
}
