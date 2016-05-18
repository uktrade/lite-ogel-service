package uk.gov.bis.lite.ogel.database;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.database.dao.LocalOgelDAO;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgelLookUp;

import java.io.IOException;
import java.util.List;

@Singleton
public class LocalOgelFlatFileImpl implements LocalOgelDAO {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalOgelFlatFileImpl.class);
  private static final String LOCAL_OGEL_CONDITION_DATA_FILE = "ogel-condition-data.json";
  private static final String LOCAL_OGEL_LOOKUP_DATA_FILE = "ogel-lookup-data.json";

  private static List<LocalOgel> localOgels;

  @Override
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
  public LocalOgel getOgelById(String ogelID) {
    if (localOgels == null) {
      getAllLocalOgels();
    }
    return localOgels.stream().filter(o -> o.getId().equalsIgnoreCase(ogelID)).findAny()
        .orElseThrow(() -> new RuntimeException("Local Spire Could not be found with given id " + ogelID));
  }

  @Override
  public LocalOgel updateOgelConditionList(String ogelID, List<String> updateData, String fieldName) throws Exception {
    final LocalOgel foundOgelCondition = getOgelById(ogelID);
    switch (fieldName) {
      case "canList":
        foundOgelCondition.getSummary().setCanList(updateData);
        break;
      case "cantList":
        foundOgelCondition.getSummary().setCantList(updateData);
        break;
      case "mustList":
        foundOgelCondition.getSummary().setMustList(updateData);
        break;
      case "howToUseList":
        foundOgelCondition.getSummary().setHowToUseList(updateData);
        break;
      default:
        LOGGER.error("Update operation unsuccessful. Invalid condition field name " + fieldName);
        throw new Exception("Invalid local ogel condition parameter " + fieldName);
    }
    return foundOgelCondition;
  }
}
