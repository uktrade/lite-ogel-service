package uk.gov.bis.lite.ogel.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ListJsonMapper {
  List<?> mapToListFromJson(String resourceName, Class expected) throws IOException {
    final String pathToLocalSpireOgelResource = this.getClass().getClassLoader().getResource(resourceName).getPath();
    String localOgelDataString = new String(Files.readAllBytes(Paths.get(pathToLocalSpireOgelResource)));

    ObjectMapper mapper = new ObjectMapper();
    return mapper.<List<LocalOgel>>readValue(localOgelDataString,
        mapper.getTypeFactory().constructCollectionType(List.class, expected));
  }
}
