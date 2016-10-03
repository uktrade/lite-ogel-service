package uk.gov.bis.lite.ogel.client.unmarshall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class SpireOgelConditionUnmarshaller implements SpireOgelUnmarshaller {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireOgelConditionUnmarshaller.class);

  private static final String RATING_LIST_EXPRESSION = "RATINGS_LIST";
  private static final String CONDITIONS_LIST_EXPRESSION = "CONDITIONS_LIST";
  private static final String EXCLUDED_COUNTRIES_EXPRESSION = "DEST_COUNTRY_EXCLUDE_LIST";
  private static final String INCLUDED_COUNTRIES_EXPRESSION = "DEST_COUNTRY_INCLUDE_LIST";
  private static final String CONDITION_NO_EXPRESSION = "CONDITION_NO";

  @Override
  public List<OgelCondition> unmarshall(XPath xpath, Node ogelNode, String xPathExpression) throws XPathExpressionException {
    final Node conditionsNode = (Node) xpath.evaluate(CONDITIONS_LIST_EXPRESSION, ogelNode, XPathConstants.NODE);
    NodeList nodeList = conditionsNode.getChildNodes();
    if (nodeList != null) {
      List<OgelCondition> conditions = new ArrayList<>();
      for (int j = 0; j < nodeList.getLength(); j++) {
        OgelCondition condition = new OgelCondition();
        Node node = nodeList.item(j);
        if (node != null) {
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            int conditionNo = Integer.parseInt(((Node) xpath.evaluate(CONDITION_NO_EXPRESSION, node, XPathConstants.NODE)).getTextContent());

            SpireOgelRatingUnmarshaller ratingUnmarshaller = new SpireOgelRatingUnmarshaller();
            condition.setRatingList(ratingUnmarshaller.unmarshall(xpath, node, RATING_LIST_EXPRESSION));

            SpireOgelCountryUnmarshaller countryUnmarshaller = new SpireOgelCountryUnmarshaller();

            List<Country> includedCountries = countryUnmarshaller.unmarshall(xpath, node, INCLUDED_COUNTRIES_EXPRESSION);
            List<Country> excludedCountries = countryUnmarshaller.unmarshall(xpath, node, EXCLUDED_COUNTRIES_EXPRESSION);

            // We expect either a list of included countries or a list of excluded countries, not both
            if(!includedCountries.isEmpty()) {
              condition.setCountries(includedCountries, OgelCondition.CountryStatus.INCLUDED);
            } else if(!excludedCountries.isEmpty()) {
              condition.setCountries(includedCountries, OgelCondition.CountryStatus.EXCLUDED);
            }
            // Logs data if both includedCountries list and excludedCountries list are populated
            logUnexpectedData(conditionNo, includedCountries, excludedCountries);

            condition.setId(conditionNo);
            conditions.add(condition);
          }
        }
      }
      return conditions;
    }
    return null;
  }

  private void logUnexpectedData(int conditionNo, List<Country> included, List<Country> excluded) {
    if(!included.isEmpty() && !excluded.isEmpty()) {
      LOGGER.warn("Retrieved condition from Spire with both included and excluded country lists: " + conditionNo);
      LOGGER.warn("Included: " + included.stream().map(Country::getId).reduce(", ", String::concat));
      LOGGER.warn("Excluded: " + excluded.stream().map(Country::getId).reduce(", ", String::concat));
    }
  }
}
