package uk.gov.bis.lite.ogel.database.unmarshall;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalOgelLookUpDeserializer extends JsonDeserializer<LocalOgel> {
  @Override
  public LocalOgel deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    LocalOgel localOgel = new LocalOgel();
    OgelConditionSummary summary = new OgelConditionSummary();
    JsonNode node = jp.getCodec().readTree(jp);
    localOgel.setName(node.get("name").asText());
    List<String> canList = getConditionList(node, "canList");
    List<String> cantList = getConditionList(node, "cantList");
    List<String> mustList = getConditionList(node, "mustList");
    List<String> howToUseList = getConditionList(node, "howToUseList");
    summary.setCanList(canList);
    summary.setCantList(cantList);
    summary.setMustList(mustList);
    summary.setHowToUseList(howToUseList);
    localOgel.setSummary(summary);
    return localOgel;
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
