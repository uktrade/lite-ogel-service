package uk.gov.bis.lite.ogel.client.unmarshall;

import com.google.common.base.Stopwatch;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
  private static final String CONDITIONS_LIST_EXPRESSION = "CONDITIONS_LIST";

  public List<SpireOgel> execute(SOAPMessage message) throws SOAPException, XPathExpressionException {

    final SOAPBody soapBody = message.getSOAPBody();
    XPath xpath = XPathFactory.newInstance().newXPath();
    NodeList nodeList = (NodeList) xpath.evaluate("//OGEL_TYPES_LIST", soapBody, XPathConstants.NODESET);

    if (nodeList != null) {
      return parseSoapBody(nodeList, xpath);
    }
    return null;
  }

  public List<SpireOgel> parseSoapBody(NodeList nodeList, XPath xpath) throws XPathExpressionException {
    List<SpireOgel> spireOgelList = new ArrayList<>();
    nodeList = nodeList.item(0).getChildNodes();
    final Stopwatch stopwatch = Stopwatch.createStarted();
    for (int i = 0; i < nodeList.getLength(); i++) {

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
        final SpireOgelConditionUnmarshaller conditionUnmarshaller = new SpireOgelConditionUnmarshaller();
        List<OgelCondition> ogelConditions = conditionUnmarshaller.unmarshall(xpath, currentOgelNode, CONDITIONS_LIST_EXPRESSION); //= new ArrayList<>();
        currentOgel.setOgelConditions(ogelConditions);
        spireOgelList.add(currentOgel);
      }
    }
    stopwatch.stop();
    System.out.println("The unmarshalling of the Spire Response took " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds ");
    return spireOgelList;
  }
}
