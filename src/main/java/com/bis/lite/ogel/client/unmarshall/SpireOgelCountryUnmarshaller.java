package com.bis.lite.ogel.client.unmarshall;

import com.bis.lite.ogel.model.Country;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

public class SpireOgelCountryUnmarshaller {

    private static final String COUNTRY_CODE_EXPRESSION = "COUNTRY_ID";
    private static final String COUNTRY_SET_ID_EXPRESSION = "COUNTRY_SET_ID";
    private static final String COUNTRY_NAME_EXPRESSION = "COUNTRY_NAME";


    public List<Country> getIncludedAndExcludedCountries(XPath xpath, Node ogelNode, String xPathExpression) throws XPathExpressionException {
        final Node specialCountriesNode = (Node) xpath.evaluate(xPathExpression, ogelNode, XPathConstants.NODE);
        NodeList specialCountriesNodeChildList = specialCountriesNode.getChildNodes();
        if (specialCountriesNodeChildList != null) {
            List<Country> excludedCountriesList = new ArrayList<>();
            //TODO change below limit
            for (int k = 1; k < 10; k = k + 2) {
                if (specialCountriesNodeChildList.getLength() > 0) {
                    System.out.println("index: " + k);
                    Node excludedCountryNode = specialCountriesNodeChildList.item(k);
                    if (excludedCountryNode != null) {
                        Country priviligedCountry = new Country();
                        priviligedCountry.setId(((Node) xpath.evaluate(COUNTRY_CODE_EXPRESSION,
                                excludedCountryNode, XPathConstants.NODE)).getTextContent());
                        priviligedCountry.setSetID(((Node) xpath.evaluate(COUNTRY_SET_ID_EXPRESSION,
                                excludedCountryNode, XPathConstants.NODE)).getTextContent());
                        priviligedCountry.setName(((Node) xpath.evaluate(COUNTRY_NAME_EXPRESSION,
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
