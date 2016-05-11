package uk.gov.bis.lite.ogel.client.unmarshall;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class SpireOgelConditionUnmarshaller implements SpireOgelUnmarshaller {
  private static final String RATING_LIST_EXPRESSION = "RATINGS_LIST";
  private static final String CONDITIONS_LIST_EXPRESSION = "CONDITIONS_LIST";
  private static final String EXCLUDED_COUNTRIES_EXPRESSION = "DEST_COUNTRY_EXCLUDE_LIST";
  private static final String INCLUDED_COUNTRIES_EXPRESSION = "DEST_COUNTRY_INCLUDE_LIST";
  private static final String CONDITION_NO_EXPRESSION = "CONDITION_NO";

  @Override
  public List<OgelCondition> unmarshall(XPath xpath, Node ogelNode, String xPathExpression) throws XPathExpressionException {
    final Node conditionsNode = (Node) xpath.evaluate(CONDITIONS_LIST_EXPRESSION, ogelNode, XPathConstants.NODE);
    NodeList conditionsListNode = conditionsNode.getChildNodes();
    if (conditionsListNode != null) {
      List<OgelCondition> ogelConditions = new ArrayList<>();
      for (int j = 0; j < conditionsListNode.getLength(); j++) {
        OgelCondition ogelCondition = new OgelCondition();
        Node singleConditionNode = conditionsListNode.item(j);
        if (singleConditionNode != null) {
          if (singleConditionNode.getNodeType() == Node.ELEMENT_NODE) {
            final SpireOgelRatingUnmarshaller spireOgelRatingUnmarshaller = new SpireOgelRatingUnmarshaller();
            final List<Rating> ratingsList = spireOgelRatingUnmarshaller.unmarshall(xpath, singleConditionNode, RATING_LIST_EXPRESSION);
            ogelCondition.setRatingList(ratingsList);
            final SpireOgelCountryUnmarshaller spireOgelCountryUnmarshaller = new SpireOgelCountryUnmarshaller();
            final List<Country> excludedCountriesList =
                spireOgelCountryUnmarshaller.unmarshall(xpath, singleConditionNode, EXCLUDED_COUNTRIES_EXPRESSION);
            final List<Country> includedCountriesList =
                spireOgelCountryUnmarshaller.unmarshall(xpath, singleConditionNode, INCLUDED_COUNTRIES_EXPRESSION);
            ogelCondition.setExcludedCountries(excludedCountriesList);
            ogelCondition.setIncludedCountries(includedCountriesList);
            ogelCondition.setId(Integer.parseInt(((Node) xpath.evaluate(CONDITION_NO_EXPRESSION,
                singleConditionNode, XPathConstants.NODE)).getTextContent()));
          }
          ogelConditions.add(ogelCondition);
        }
      }
      return ogelConditions;
    }
    return null;
  }
}
