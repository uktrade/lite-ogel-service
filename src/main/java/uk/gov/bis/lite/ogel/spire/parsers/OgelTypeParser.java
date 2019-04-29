package uk.gov.bis.lite.ogel.spire.parsers;

import com.google.common.base.Stopwatch;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.bis.lite.common.spire.client.SpireResponse;
import uk.gov.bis.lite.common.spire.client.exception.SpireClientException;
import uk.gov.bis.lite.common.spire.client.parser.SpireParser;
import uk.gov.bis.lite.ogel.model.ActivityType;
import uk.gov.bis.lite.ogel.model.Country;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class OgelTypeParser implements SpireParser<List<SpireOgel>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OgelTypeParser.class);
  private final XPath xpath = XPathFactory.newInstance().newXPath();

  @Override
  public List<SpireOgel> parseResponse(SpireResponse spireResponse) {
    return getSitesFromNodes(spireResponse.getElementChildNodesForList("//OGEL_TYPES_LIST"));
  }

  private List<SpireOgel> getSitesFromNodes(List<Node> nodes) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    List<SpireOgel> ogels = parseSoapBody(nodes);
    stopwatch.stop();
    LOGGER.info("The unmarshalling of the Spire Response took {} milliseconds", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return ogels;
  }

  private List<SpireOgel> parseSoapBody(List<Node> nodes) {
    List<SpireOgel> spireOgels = new ArrayList<>();

    for (Node node : nodes) {
      SpireOgel currentOgel = new SpireOgel();
      Node currentNode = node.cloneNode(true); //Performance enhancement
      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
        try {
          currentOgel.setId(((Node) xpath.evaluate("OGEL_TYPE_REF", currentNode, XPathConstants.NODE)).getTextContent());
          currentOgel.setName(((Node) xpath.evaluate("NAME", currentNode, XPathConstants.NODE)).getTextContent());

          Node linkToNode = (Node) xpath.evaluate("LINK_TO_OGL", currentNode, XPathConstants.NODE);
          if (linkToNode != null) {
            currentOgel.setLink(linkToNode.getTextContent());
          }
          Node activityNode = (Node) xpath.evaluate("OGL_ACTIVITY", currentNode, XPathConstants.NODE);
          if (activityNode != null) {
            currentOgel.setActivityType(ActivityType.valueOf(activityNode.getTextContent()));
          }
          Node lastUpdatedNode = (Node) xpath.evaluate("LAST_UPDATED_DATE", currentNode, XPathConstants.NODE);
          if (lastUpdatedNode != null) {
            currentOgel.setLastUpdatedDate(lastUpdatedNode.getTextContent());
          }
          List<OgelCondition> ogelConditions = unmarshallOgelConditions(currentNode);
          currentOgel.setOgelConditions(ogelConditions);

          int ranking = unmarshallRanking(currentNode, currentOgel.getId());
          currentOgel.setRanking(ranking);
          spireOgels.add(currentOgel);
        } catch (XPathExpressionException e) {
          throw new SpireClientException("Error while parsing SOAP response body");
        }
      }
    }
    return spireOgels;
  }

  private int unmarshallRanking(Node ogelNode, String ogelId) throws XPathExpressionException {
    final String elementName = "OGL_RANKING";
    final int defaultValue = 999;
    final Node rankingNode = (Node) xpath.evaluate(elementName, ogelNode, XPathConstants.NODE);
    if (rankingNode != null) {
      if (StringUtils.isNotEmpty(rankingNode.getTextContent())) {
        try {
          return Integer.parseInt(rankingNode.getTextContent());
        } catch (NumberFormatException ex) {
          LOGGER.info("{} element for {} contains an invalid number, using {} instead", elementName, ogelId, defaultValue);
          return defaultValue;
        }
      } else {
        LOGGER.info("{} element for {} is empty, using {} instead", elementName, ogelId, defaultValue);
        return defaultValue;
      }
    } else {
      LOGGER.info("{} element not found for {}, using {} instead", elementName, ogelId, defaultValue);
      return defaultValue;
    }
  }

  private List<OgelCondition> unmarshallOgelConditions(Node ogelNode) throws XPathExpressionException {
    final Node conditionsNode = (Node) xpath.evaluate("CONDITIONS_LIST", ogelNode, XPathConstants.NODE);
    NodeList nodeList = conditionsNode.getChildNodes();
    List<OgelCondition> conditions = new ArrayList<>();
    if (nodeList != null) {
      for (int j = 0; j < nodeList.getLength(); j++) {
        OgelCondition condition = new OgelCondition();
        Node node = nodeList.item(j);
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {

          int conditionNo = Integer.parseInt(((Node) xpath.evaluate("CONDITION_NO", node, XPathConstants.NODE)).getTextContent());

          List<Rating> includedRatings = unmarshallRatings(node, "RATINGS_LIST");
          List<Rating> excludedRatings = unmarshallRatings(node, "EXCLUDED_RATINGS_LIST");
          Set<String> excludedRatingCodes = excludedRatings.stream().map(Rating::getRatingCode).collect(Collectors.toSet());
          condition.setRatingList(
            includedRatings.stream()
              .filter(rating -> !excludedRatingCodes.contains(rating.getRatingCode()))
              .collect(Collectors.toList())
          );

          // Included and excluded countries
          List<Country> includedCountries = unmarshallCountries(node, "DEST_COUNTRY_INCLUDE_LIST");
          List<Country> excludedCountries = unmarshallCountries(node, "DEST_COUNTRY_EXCLUDE_LIST");

          // We expect either a list of included countries or a list of excluded countries, not both
          if (includedCountries != null && !includedCountries.isEmpty()) {
            condition.setCountries(includedCountries, OgelCondition.CountryStatus.INCLUDED);
          } else if (excludedCountries != null && !excludedCountries.isEmpty()) {
            condition.setCountries(excludedCountries, OgelCondition.CountryStatus.EXCLUDED);
          }
          // Logs data if both includedCountries list and excludedCountries list are populated
          logUnexpectedData(conditionNo, includedCountries, excludedCountries);

          condition.setId(conditionNo);
          conditions.add(condition);
        }
      }
    }
    return conditions;
  }

  private List<Rating> unmarshallRatings(Node ogelNode, String xPathExpression) throws XPathExpressionException {
    List<Rating> ratings = new ArrayList<>();
    final Node ratingListNode = (Node) xpath.evaluate(xPathExpression, ogelNode, XPathConstants.NODE);
    NodeList conditionsNodeList = ratingListNode.getChildNodes();
    for (int j = 0; j < conditionsNodeList.getLength(); j++) {
      Node ratingNode = conditionsNodeList.item(j).cloneNode(true);
      if (ratingNode != null && ratingNode.getNodeType() == Node.ELEMENT_NODE) {
        Rating rating = new Rating();
        rating.setRatingCode(((Node) xpath.evaluate("RATING_NAME", ratingNode, XPathConstants.NODE)).getTextContent());
        rating.setConditionalRating(Boolean.parseBoolean(((Node) xpath.evaluate("CONDITIONAL_RATING", ratingNode, XPathConstants.NODE)).getTextContent()));
        ratings.add(rating);
      }
    }
    return ratings;
  }

  private List<Country> unmarshallCountries(Node ogelNode, String xPathExpression) throws XPathExpressionException {
    Node countriesNode = (Node) xpath.evaluate(xPathExpression, ogelNode, XPathConstants.NODE);
    NodeList countriesNodeList = countriesNode.getChildNodes();
    if (countriesNodeList != null) {
      List<Country> excludedCountriesList = new ArrayList<>();
      for (int k = 1; k < countriesNodeList.getLength(); k = k + 2) {
        if (countriesNodeList.getLength() > 0) {
          Node countryNode = countriesNodeList.item(k).cloneNode(true);
          if (countryNode != null) {
            Country country = new Country();
            country.setId(((Node) xpath.evaluate("COUNTRY_ID", countryNode, XPathConstants.NODE)).getTextContent());
            country.setSetID(((Node) xpath.evaluate("COUNTRY_SET_ID", countryNode, XPathConstants.NODE)).getTextContent());
            country.setName(((Node) xpath.evaluate("COUNTRY_NAME", countryNode, XPathConstants.NODE)).getTextContent());
            excludedCountriesList.add(country);
          }
        }
      }
      return excludedCountriesList;
    }
    return null;
  }

  private void logUnexpectedData(int conditionNo, List<Country> included, List<Country> excluded) {
    if (!CollectionUtils.isEmpty(included) && !CollectionUtils.isEmpty(excluded)) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Retrieved condition from Spire with both included and excluded country lists: {}", conditionNo);
        LOGGER.warn("Included: {}", included.stream().map(Country::getId).reduce(", ", String::concat));
        LOGGER.warn("Excluded: {}", excluded.stream().map(Country::getId).reduce(", ", String::concat));
      }
    }
  }
}
