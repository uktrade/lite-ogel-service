package com.bis.lite.ogel.client.unmarshall;

import com.google.inject.Inject;

import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.SpireOgel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class SpireOgelSOAPUnmarshaller {

    private static final String codeExpression = "OGEL_TYPE_REF";
    private static final String nameExpression = "NAME";
    private static final String linkToOgelExpression = "LINK_TO_OGL";
    private static final String CATEGORY_EXPRESSION = "OGL_ACTIVITY";
    private static final String RATING_LIST_EXPRESSION = "CONDITIONS_LIST/CONDITION/RATINGS_LIST";
    private static final String EXCLUDED_COUNTRIES_EXPRESSION = "CONDITIONS_LIST/CONDITION/DEST_COUNTRY_EXCLUDE_LIST";
    private static final String INCLUDED_COUNTRIES_EXPRESSION = "CONDITIONS_LIST/CONDITION/DEST_COUNTRY_INCLUDE_LIST";
    private static final String RATING_CODE_EXPRESSION = "RATING_NAME";

    public List<SpireOgel> execute(SOAPMessage message) throws SOAPException, XPathExpressionException {

        final SOAPBody soapBody = message.getSOAPBody();
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate("//OGEL_TYPES_LIST", soapBody, XPathConstants.NODESET);

        return parseSoapBody(nodeList, xpath);
    }

    public List<SpireOgel> parseSoapBody(NodeList nodeList, XPath xpath) throws XPathExpressionException {
        List<SpireOgel> spireOgelList = new ArrayList<>();
        nodeList = nodeList.item(0).getChildNodes();

        for (int i = 1; i < nodeList.getLength(); i = i + 2) {
            long tStart = System.currentTimeMillis();
            SpireOgel currentOgel = new SpireOgel();
            Node currentOgelNode = nodeList.item(i).cloneNode(true);
            currentOgel.setId(((Node) xpath.evaluate(codeExpression, currentOgelNode, XPathConstants.NODE)).getTextContent());
            currentOgel.setDescription(((Node) xpath.evaluate(nameExpression, currentOgelNode, XPathConstants.NODE)).getTextContent());
            final Node linkToOgelNode = (Node) xpath.evaluate(linkToOgelExpression, currentOgelNode, XPathConstants.NODE);
            if (linkToOgelNode != null) {
                currentOgel.setLink(linkToOgelNode.getTextContent());
            }
            final Node ogelCategoryNode = (Node) xpath.evaluate(CATEGORY_EXPRESSION, currentOgelNode, XPathConstants.NODE);
            if (ogelCategoryNode != null) {
                currentOgel.setCategory(ogelCategoryNode.getTextContent());
            }
            final Node ratingsNode = (Node) xpath.evaluate(RATING_LIST_EXPRESSION, currentOgelNode, XPathConstants.NODE);
            NodeList ratingsListNode = ratingsNode.getChildNodes();
            if (ratingsListNode != null) {
                List<String> ratingsList = new ArrayList<>();
                for (int j = 1; j < 50; j = j + 2) {
                    Node ratingNode = ratingsListNode.item(j);
                    if (ratingNode != null) {
                        ratingsList.add(((Node) xpath.evaluate(RATING_CODE_EXPRESSION, ratingNode, XPathConstants.NODE)).getTextContent());
                    }
                }
                currentOgel.setRatingCodes(ratingsList);
            }
            final SpireOgelCountryUnmarshaller spireOgelCountryUnmarshaller = new SpireOgelCountryUnmarshaller();
            final List<Country> excludedCountriesList = spireOgelCountryUnmarshaller.getIncludedAndExcludedCountries(xpath, currentOgelNode, EXCLUDED_COUNTRIES_EXPRESSION);
            final List<Country> includedCountriesList = spireOgelCountryUnmarshaller.getIncludedAndExcludedCountries(xpath, currentOgelNode, INCLUDED_COUNTRIES_EXPRESSION);
            currentOgel.setExcludedCountries(excludedCountriesList);
            currentOgel.setIncludedCountries(includedCountriesList);

            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            System.out.println("New Ogel Added to the List in " + elapsedSeconds + " seconds " + currentOgel);

            spireOgelList.add(currentOgel);
        }
        return spireOgelList;
    }
}
