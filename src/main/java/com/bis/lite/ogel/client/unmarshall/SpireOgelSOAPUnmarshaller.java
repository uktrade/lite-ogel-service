package com.bis.lite.ogel.client.unmarshall;

import com.bis.lite.ogel.model.CategoryType;
import com.bis.lite.ogel.model.Country;
import com.bis.lite.ogel.model.OgelCondition;
import com.bis.lite.ogel.model.Rating;
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
    private static final String RATING_LIST_EXPRESSION = "RATINGS_LIST";
    private static final String CONDITIONS_LIST_EXPRESSION = "CONDITIONS_LIST";
    private static final String EXCLUDED_COUNTRIES_EXPRESSION = "DEST_COUNTRY_EXCLUDE_LIST";
    private static final String INCLUDED_COUNTRIES_EXPRESSION = "DEST_COUNTRY_INCLUDE_LIST";
    private static final String CONDITION_NO_EXPRESSION = "CONDITION_NO";

    public List<SpireOgel> execute(SOAPMessage message) throws SOAPException, XPathExpressionException {

        final SOAPBody soapBody = message.getSOAPBody();
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate("//OGEL_TYPES_LIST", soapBody, XPathConstants.NODESET);

        return parseSoapBody(nodeList, xpath);
    }

    public List<SpireOgel> parseSoapBody(NodeList nodeList, XPath xpath) throws XPathExpressionException {
        List<SpireOgel> spireOgelList = new ArrayList<>();
        nodeList = nodeList.item(0).getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            long tStart = System.currentTimeMillis();
            SpireOgel currentOgel = new SpireOgel();
            Node currentOgelNode = nodeList.item(i).cloneNode(true);
            if (currentOgelNode.getNodeType() == Node.ELEMENT_NODE) {
                currentOgel.setId(((Node) xpath.evaluate(codeExpression, currentOgelNode, XPathConstants.NODE)).getTextContent());
                currentOgel.setDescription(((Node) xpath.evaluate(nameExpression, currentOgelNode, XPathConstants.NODE)).getTextContent());
                final Node linkToOgelNode = (Node) xpath.evaluate(linkToOgelExpression, currentOgelNode, XPathConstants.NODE);
                if (linkToOgelNode != null) {
                    currentOgel.setLink(linkToOgelNode.getTextContent());
                }
                final Node ogelCategoryNode = (Node) xpath.evaluate(CATEGORY_EXPRESSION, currentOgelNode, XPathConstants.NODE);
                if (ogelCategoryNode != null) {
                    currentOgel.setCategory(CategoryType.valueOf(ogelCategoryNode.getTextContent()));
                }
                final Node conditionsNode = (Node) xpath.evaluate(CONDITIONS_LIST_EXPRESSION, currentOgelNode, XPathConstants.NODE);
                NodeList conditionsListNode = conditionsNode.getChildNodes();
                if (conditionsListNode != null) {
                    List<OgelCondition> ogelConditions = new ArrayList<>();
                    for (int j = 0; j < conditionsListNode.getLength(); j++) {
                        OgelCondition ogelCondition = new OgelCondition();
                        Node singleConditionNode = conditionsListNode.item(j);
                        if(singleConditionNode.getNodeType() == Node.ELEMENT_NODE) {
                            if (singleConditionNode != null) {
                                final SpireOgelRatingUnmarshaller spireOgelRatingUnmarshaller = new SpireOgelRatingUnmarshaller();
                                final List<Rating> ratingsList = spireOgelRatingUnmarshaller.getRatingsFromRatingsList(xpath, singleConditionNode, RATING_LIST_EXPRESSION);
                                ogelCondition.setRatingList(ratingsList);
                                final SpireOgelCountryUnmarshaller spireOgelCountryUnmarshaller = new SpireOgelCountryUnmarshaller();
                                final List<Country> excludedCountriesList =
                                        spireOgelCountryUnmarshaller.getIncludedAndExcludedCountries(xpath, singleConditionNode, EXCLUDED_COUNTRIES_EXPRESSION);
                                final List<Country> includedCountriesList =
                                        spireOgelCountryUnmarshaller.getIncludedAndExcludedCountries(xpath, singleConditionNode, INCLUDED_COUNTRIES_EXPRESSION);
                                ogelCondition.setExcludedCountries(excludedCountriesList);
                                ogelCondition.setIncludedCountries(includedCountriesList);
                                ogelCondition.setId(Integer.parseInt(((Node) xpath.evaluate(CONDITION_NO_EXPRESSION,
                                        singleConditionNode, XPathConstants.NODE)).getTextContent()));
                            }
                            ogelConditions.add(ogelCondition);
                        }
                    }
                    currentOgel.setOgelConditions(ogelConditions);
                }
                long tEnd = System.currentTimeMillis();
                long tDelta = tEnd - tStart;
                double elapsedSeconds = tDelta / 1000.0;
                System.out.println("New Ogel Added to the List in " + elapsedSeconds + " seconds " + currentOgel);

                spireOgelList.add(currentOgel);
            }
        }
        return spireOgelList;
    }
}
