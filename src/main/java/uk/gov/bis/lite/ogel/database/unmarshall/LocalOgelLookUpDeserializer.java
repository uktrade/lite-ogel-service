package uk.gov.bis.lite.ogel.database.unmarshall;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.gov.bis.lite.ogel.model.localOgel.LocalSpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalOgelLookUpDeserializer extends JsonDeserializer<LocalSpireOgel> {
  @Override
  public LocalSpireOgel deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    LocalSpireOgel localSpireOgel = new LocalSpireOgel();
    OgelSummary summary = new OgelSummary();
    JsonNode node = jp.getCodec().readTree(jp);
    localSpireOgel.setName(node.get("name").asText());
    List<String> canList = getConditionList(node, "canList");
    List<String> cantList = getConditionList(node, "cantList");
    List<String> mustList = getConditionList(node, "mustList");
    List<String> howToUseList = getConditionList(node, "howToUseList");
    summary.setCanList(canList);
    summary.setCantList(cantList);
    summary.setMustList(mustList);
    summary.setHowToUseList(howToUseList);
    localSpireOgel.setSummary(summary);
    return localSpireOgel;
  }

  private List<String> getConditionList(JsonNode node, String fieldName){
    final JsonNode canListNode = node.get(fieldName);
    List<String> conditionList = new ArrayList<>();
    for (final JsonNode objNode : canListNode) {
      conditionList.add(objNode.asText());
    }
    return conditionList;
  }
}
