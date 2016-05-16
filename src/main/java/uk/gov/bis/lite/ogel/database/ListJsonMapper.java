package uk.gov.bis.lite.ogel.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ListJsonMapper {
  public List<? extends Object> mapToListFromJson(String resourceName, Class expected) throws IOException {
    final String pathToLocalSpireOgelResource = this.getClass().getClassLoader().getResource(resourceName).getPath();
    String localOgelDataString = new String(Files.readAllBytes(Paths.get(pathToLocalSpireOgelResource)));

    ObjectMapper mapper = new ObjectMapper();
    List<LocalSpireOgel> localSpireOgelList = mapper.readValue(localOgelDataString,
        mapper.getTypeFactory().constructCollectionType(List.class, expected));
    return localSpireOgelList;
  }
}
