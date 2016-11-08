package uk.gov.bis.lite.ogel.client.unmarshall;

import uk.gov.bis.lite.ogel.model.Rating;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

public class SpireOgelRatingUnmarshaller implements SpireOgelUnmarshaller {

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
          newRating.setRatingCode(((Node) xpath.evaluate("RATING_NAME", ratingNode, XPathConstants.NODE)).getTextContent());
          newRating.setConditionalRating(Boolean.parseBoolean(((Node) xpath.evaluate("CONDITIONAL_RATING", ratingNode, XPathConstants.NODE)).getTextContent()));
          ratingsList.add(newRating);
        }
      }
    }
    return ratingsList;
  }
}
