package uk.gov.bis.lite.ogel.client.unmarshall;

import org.w3c.dom.Node;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;


public interface SpireOgelUnmarshaller {

  List<? extends Object> unmarshall(XPath xpath, Node ogelNode, String xPathExpression) throws XPathExpressionException;
}
