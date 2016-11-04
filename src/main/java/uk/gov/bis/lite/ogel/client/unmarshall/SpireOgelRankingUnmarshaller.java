package uk.gov.bis.lite.ogel.client.unmarshall;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

// Note: does NOT implement SpireOgelUnmarshaller interface
public class SpireOgelRankingUnmarshaller {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireOgelRankingUnmarshaller.class);
  private static final String OGL_RANKING_ELEMENT_NAME = "OGL_RANKING";
  private static final String OGL_RANKING_EXPRESSION = OGL_RANKING_ELEMENT_NAME;
  private static final int RANKING_DEFAULT = 999;

  static public int unmarshall(XPath xpath, Node ogelNode, String ogelId) throws XPathExpressionException {
    final Node rankingNode = (Node) xpath.evaluate(OGL_RANKING_EXPRESSION, ogelNode, XPathConstants.NODE);
    if (rankingNode != null) {
      if (StringUtils.isNotEmpty(rankingNode.getTextContent())) {
        try {
          return Integer.parseInt(rankingNode.getTextContent());
        }
        catch (NumberFormatException ex) {
          LOGGER.info(String.format("%s element for %s contains an invalid number, using %d instead",
              OGL_RANKING_ELEMENT_NAME, ogelId, RANKING_DEFAULT));
          return RANKING_DEFAULT;
        }
      }
      else {
        LOGGER.info(String.format("%s element for %s is empty, using %d instead", OGL_RANKING_ELEMENT_NAME, ogelId,
            RANKING_DEFAULT));
        return RANKING_DEFAULT;
      }
    }
    else {
      LOGGER.info(String.format("%s element not found for %s, using %d instead", OGL_RANKING_ELEMENT_NAME, ogelId,
          RANKING_DEFAULT));
      return RANKING_DEFAULT;
    }
  }
}
