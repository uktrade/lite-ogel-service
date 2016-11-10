package uk.gov.bis.lite.ogel.unmarshall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.bis.lite.ogel.client.unmarshall.SpireOgelSOAPUnmarshaller;
import uk.gov.bis.lite.ogel.model.OgelCondition;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class SpireOgelSOAPUnmarshallerTest {

  private List<SpireOgel> ogels;

  private String OGL0 = "OGL0";
  private String OGL1 = "OGL1";
  private String OGL2 = "OGL2";
  private String OGL3 = "OGL3";
  private String OGL4 = "OGL4";

  private String EXCLUDED = "[EXCLUDED]";
  private String INCLUDED = "[INCLUDED]";

  @Before
  public void setUp()  throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(ClassLoader.getSystemResourceAsStream("OgelTypes01234Response.xml"));
    XPath xPath = XPathFactory.newInstance().newXPath();
    NodeList nodeList = (NodeList) xPath.compile("//OGEL_TYPES_LIST").evaluate(document, XPathConstants.NODESET);
    ogels = new SpireOgelSOAPUnmarshaller().parseSoapBody(nodeList, xPath);
  }

  @Test
  public void testOgelsList() {
    assertTrue(!ogels.isEmpty());
    assertEquals(5, ogels.size());
    assertThat(ogels).extracting(SpireOgel::getId).containsOnly(OGL0, OGL1, OGL2, OGL3, OGL4);
  }

  @Test
  public void testCountriesIncludedExcluded() {
    assertThat(ogels).filteredOn("id", OGL0).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", OGL0).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(EXCLUDED);
    assertThat(ogels).filteredOn("id", OGL0).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(32);

    assertThat(ogels).filteredOn("id", OGL1).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", OGL1).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(INCLUDED);
    assertThat(ogels).filteredOn("id", OGL1).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(44);

    assertThat(ogels).filteredOn("id", OGL2).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", OGL2).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(EXCLUDED);
    assertThat(ogels).filteredOn("id", OGL2).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(62);

    assertThat(ogels).filteredOn("id", OGL3).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", OGL3).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(INCLUDED);
    assertThat(ogels).filteredOn("id", OGL3).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(44);

    assertThat(ogels).filteredOn("id", OGL4).extracting(SpireOgel::getOgelConditions).hasSize(1);
    assertThat(ogels).filteredOn("id", OGL4).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).extracting("countryStatus").hasToString(EXCLUDED);
    assertThat(ogels).filteredOn("id", OGL4).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getCountries).asList().hasSize(41);
  }

  @Test
  public void testRatings() {
    assertThat(ogels).filteredOn("id", OGL0).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(10);

    assertThat(ogels).filteredOn("id", OGL1).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(79);

    assertThat(ogels).filteredOn("id", OGL2).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(284);

    assertThat(ogels).filteredOn("id", OGL3).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(309);

    assertThat(ogels).filteredOn("id", OGL4).extracting(SpireOgel::getOgelConditions)
        .extracting(cons -> cons.get(0)).flatExtracting(OgelCondition::getRatingList).asList().hasSize(80);
  }

  @Test
  public void testRankings() {
    assertThat(ogels.stream().filter(o -> o.getId().equals(OGL0)).findFirst().get().getRanking()).isEqualTo(1);

    assertThat(ogels.stream().filter(o -> o.getId().equals(OGL1)).findFirst().get().getRanking()).isEqualTo(2);

    assertThat(ogels.stream().filter(o -> o.getId().equals(OGL2)).findFirst().get().getRanking()).isEqualTo(3);

    assertThat(ogels.stream().filter(o -> o.getId().equals(OGL3)).findFirst().get().getRanking()).isEqualTo(999);

    assertThat(ogels.stream().filter(o -> o.getId().equals(OGL4)).findFirst().get().getRanking()).isEqualTo(999);
  }

}
