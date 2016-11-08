package uk.gov.bis.lite.ogel.client.unmarshall;

import uk.gov.bis.lite.ogel.model.Country;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class SpireOgelCountryUnmarshaller implements SpireOgelUnmarshaller {

  @Override
  public List<Country> unmarshall(XPath xpath, Node ogelNode, String xPathExpression) throws XPathExpressionException {
    final Node specialCountriesNode = (Node) xpath.evaluate(xPathExpression, ogelNode, XPathConstants.NODE);
    NodeList specialCountriesNodeChildList = specialCountriesNode.getChildNodes();
    if (specialCountriesNodeChildList != null) {
      List<Country> excludedCountriesList = new ArrayList<>();
      for (int k = 1; k < specialCountriesNodeChildList.getLength(); k = k + 2) {
        if (specialCountriesNodeChildList.getLength() > 0) {
          Node excludedCountryNode = specialCountriesNodeChildList.item(k).cloneNode(true);
          if (excludedCountryNode != null) {
            Country priviligedCountry = new Country();
            priviligedCountry.setId(((Node) xpath.evaluate("COUNTRY_ID",
                excludedCountryNode, XPathConstants.NODE)).getTextContent());
            priviligedCountry.setSetID(((Node) xpath.evaluate("COUNTRY_SET_ID",
                excludedCountryNode, XPathConstants.NODE)).getTextContent());
            priviligedCountry.setName(((Node) xpath.evaluate("COUNTRY_NAME",
                excludedCountryNode, XPathConstants.NODE)).getTextContent());
            excludedCountriesList.add(priviligedCountry);
          }
        }
      }
      return excludedCountriesList;
    }
    return null;
  }
}
