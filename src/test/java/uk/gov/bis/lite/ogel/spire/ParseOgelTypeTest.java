package uk.gov.bis.lite.ogel.spire;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.Rating;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.spire.parsers.OgelTypeParser;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class ParseOgelTypeTest extends SpireParseTest {

  private static final String A1 = "A1";
  private static final String A2 = "A2";
  private static final String A3 = "A3";

  private List<SpireOgel> ogels;

  @Before
  public void setUp() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
    ogels = new OgelTypeParser().parseResponse(createSpireResponse("spire/ogelTypes.xml"));
  }

  @Test
  public void testOgelsList() {
    assertTrue(!ogels.isEmpty());
    assertEquals(3, ogels.size());
    assertThat(ogels).extracting(SpireOgel::getId).containsOnly(A1, A2, A3);
  }

  @Test
  public void testRankings() {
    assertThat(ogels.stream().filter(o -> o.getId().equals(A1)).findFirst().get().getRanking()).isEqualTo(111);
    assertThat(ogels.stream().filter(o -> o.getId().equals(A2)).findFirst().get().getRanking()).isEqualTo(222);
    assertThat(ogels.stream().filter(o -> o.getId().equals(A3)).findFirst().get().getRanking()).isEqualTo(333);
  }

  @Test
  public void testRatings() {
    assertThat(ogels).filteredOn("id", A1).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(2);
    assertThat(ogels).filteredOn("id", A2).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(3);
    assertThat(ogels).filteredOn("id", A3).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(4);
  }

  @Test
  public void excludedRatingsAreExcludedFromRatingsList() {
    assertOgelConditionDoesNotContainRating(A1, "RATING2");
    assertOgelConditionDoesNotContainRating(A2, "RATING5");
    assertOgelConditionDoesNotContainRating(A3, "RATING8");
  }

  private void assertOgelConditionDoesNotContainRating(String ogelId, String rating) {
    assertThat(ogels).filteredOn("id", ogelId).extracting(SpireOgel::getOgelConditions)
        .extracting(conditions -> conditions.get(0)).flatExtracting(OgelCondition::getRatingList)
        .extracting(Rating::getRatingCode).doesNotContain(rating);
  }

  @Test
  public void testCountriesIncludedExcluded() {

    String EXCLUDED = "[EXCLUDED]";
    String INCLUDED = "[INCLUDED]";

    assertThat(ogels).filteredOn("id", A1).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", A1).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(EXCLUDED);
    assertThat(ogels).filteredOn("id", A1).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(3);

    assertThat(ogels).filteredOn("id", A2).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", A2).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(INCLUDED);
    assertThat(ogels).filteredOn("id", A2).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(4);

    assertThat(ogels).filteredOn("id", A3).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", A3).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(EXCLUDED);
    assertThat(ogels).filteredOn("id", A3).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(5);
  }
}
