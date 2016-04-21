package com.bis.lite.ogel.client.unmarshall;

import com.bis.lite.ogel.model.Rating;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class SpireOgelRatingUnmarshaller implements SpireOgelUnmarshaller{

    private static final String RATING_CODE_EXPRESSION = "RATING_NAME";
    private static final String RATING_CONDITION_EXPRESSION = "CONDITIONAL_RATING";

    @Override
    public List<Rating> unmarshall(XPath xpath, Node ogelNode, String xPathExpression) throws XPathExpressionException {
        List<Rating> ratingsList = new ArrayList<>();
        final Node ratingListNode = (Node) xpath.evaluate(xPathExpression, ogelNode, XPathConstants.NODE);
        NodeList conditionsListNode = ratingListNode.getChildNodes();
        for (int j = 0; j < conditionsListNode.getLength(); j++) {
            Node ratingNode = conditionsListNode.item(j).cloneNode(true);
            if (ratingNode != null) {
                if (ratingNode.getNodeType() == Node.ELEMENT_NODE) {
                    Rating newRating = new Rating();
                    newRating.setRatingCode(((Node) xpath.evaluate(RATING_CODE_EXPRESSION, ratingNode, XPathConstants.NODE)).getTextContent());
                    newRating.setConditionalRating(Boolean.parseBoolean(((Node) xpath.evaluate(RATING_CONDITION_EXPRESSION, ratingNode, XPathConstants.NODE)).getTextContent()));
                    ratingsList.add(newRating);
                }
            }
        }
        return ratingsList;
    }
}
