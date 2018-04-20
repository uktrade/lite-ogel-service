package uk.gov.bis.lite.ogel.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.api.view.ApplicableOgelView;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgelConditionSummary;

import java.util.Arrays;
import java.util.Collections;

public class ViewFactoryTest {

  private SpireOgel spireOgel;

  @Before
  public void setup() {
    spireOgel = new SpireOgel();
    spireOgel.setId("OGL1_SPIRE");
    spireOgel.setName("Spire OGEL name");
    spireOgel.setLink("http://www.ogels.com/OGL1");
  }

  @Test
  public void testCreateOgel() throws Exception {
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId("OGL1_LOCAL");
    localOgel.setName("Local OGEL name");
    LocalOgelConditionSummary summary = new LocalOgelConditionSummary();
    summary.setCanList(Collections.singletonList("Can 1"));
    summary.setCantList(Collections.singletonList("Cant 1"));
    summary.setMustList(Collections.singletonList("Must 1"));
    summary.setHowToUseList(Collections.singletonList("How to use 1"));
    localOgel.setSummary(summary);

    OgelFullView fullView = ViewFactory.createOgel(spireOgel, localOgel);

    //Note precedence of SPIRE/local attributes
    assertThat(fullView.getId()).isEqualTo("OGL1_SPIRE");
    assertThat(fullView.getName()).isEqualTo("Local OGEL name");
    assertThat(fullView.getLink()).isEqualTo("http://www.ogels.com/OGL1");
    assertThat(fullView.getSummary().getCanList()).containsOnly("Can 1");
    assertThat(fullView.getSummary().getCantList()).containsOnly("Cant 1");
    assertThat(fullView.getSummary().getMustList()).containsOnly("Must 1");
    assertThat(fullView.getSummary().getHowToUseList()).containsOnly("How to use 1");
  }

  @Test
  public void testCreateOgel_NullLocalOgel() throws Exception {
    OgelFullView fullView = ViewFactory.createOgel(spireOgel, null);

    assertThat(fullView.getId()).isEqualTo("OGL1_SPIRE");
    assertThat(fullView.getName()).isEqualTo("Spire OGEL name");
    assertThat(fullView.getLink()).isEqualTo("http://www.ogels.com/OGL1");
    assertThat(fullView.getSummary().getCanList()).isEmpty();
    assertThat(fullView.getSummary().getCantList()).isEmpty();
    assertThat(fullView.getSummary().getMustList()).isEmpty();
    assertThat(fullView.getSummary().getHowToUseList()).isEmpty();
  }

  @Test
  public void testApplicableOgel() throws Exception {
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId("OGL1_LOCAL");
    localOgel.setName("Local OGEL name");
    LocalOgelConditionSummary summary = new LocalOgelConditionSummary();
    summary.setCanList(Collections.singletonList("Can 1"));
    summary.setCantList(Collections.singletonList("Cant 1"));
    summary.setMustList(Collections.singletonList("Must 1"));
    summary.setHowToUseList(Collections.singletonList("How to use 1"));
    localOgel.setSummary(summary);

    ApplicableOgelView applicableOgel = ViewFactory.createApplicableOgel(spireOgel, localOgel);

    //Note precedence of SPIRE/local attributes
    assertThat(applicableOgel.getId()).isEqualTo("OGL1_SPIRE");
    assertThat(applicableOgel.getName()).isEqualTo("Local OGEL name");
    assertThat(applicableOgel.getUsageSummary()).containsOnly("Can 1");
  }

  @Test
  public void testApplicableOgel_NullLocalOgel() throws Exception {
    ApplicableOgelView applicableOgel = ViewFactory.createApplicableOgel(spireOgel, null);

    assertThat(applicableOgel.getId()).isEqualTo("OGL1_SPIRE");
    assertThat(applicableOgel.getName()).isEqualTo("Spire OGEL name");
    assertThat(applicableOgel.getUsageSummary()).isEmpty();
  }

  @Test
  public void testControlCodeConditionFullView() throws Exception {

    LocalControlCodeCondition localControlCodeCondition = new LocalControlCodeCondition();
    localControlCodeCondition.setOgelID("OGL1");
    localControlCodeCondition.setControlCode("CC1");
    localControlCodeCondition.setConditionDescription("CC description");
    localControlCodeCondition.setItemsAllowed(true);

    ControlCodeConditionFullView conditionFullView = ViewFactory.createControlCodeCondition(localControlCodeCondition);

    assertThat(conditionFullView.getOgelId()).isEqualTo("OGL1");
    assertThat(conditionFullView.getControlCode()).isEqualTo("CC1");
    assertThat(conditionFullView.getConditionDescription()).isEqualTo("CC description");
    assertThat(conditionFullView.isItemsAllowed()).isTrue();
    assertThat(conditionFullView.getConditionDescriptionControlCodes()).isNull();
  }

  @Test
  public void testControlCodeConditionFullViewWithBulkCodes() throws Exception {

    LocalControlCodeCondition localControlCodeCondition = new LocalControlCodeCondition();
    localControlCodeCondition.setOgelID("OGL1");
    localControlCodeCondition.setControlCode("CC1");
    localControlCodeCondition.setConditionDescription("CC description");
    localControlCodeCondition.setItemsAllowed(true);

    ControlCodeFullView cc1 = new ControlCodeFullView();
    cc1.setId("CCID1");
    cc1.setControlCode("CC1");
    cc1.setFriendlyDescription("Friendly 1");

    ControlCodeFullView cc2 = new ControlCodeFullView();
    cc2.setId("CCID2");
    cc2.setControlCode("CC2");
    cc2.setFriendlyDescription("Friendly 2");

    BulkControlCodes bulkControlCodes = new BulkControlCodes();
    bulkControlCodes.setControlCodeFullViews(Arrays.asList(cc1, cc2));

    bulkControlCodes.setMissingControlCodes(Arrays.asList("MISSING1", "MISSING2"));

    ControlCodeConditionFullView conditionFullView = ViewFactory.createControlCodeCondition(localControlCodeCondition, bulkControlCodes);

    assertThat(conditionFullView.getOgelId()).isEqualTo("OGL1");
    assertThat(conditionFullView.getControlCode()).isEqualTo("CC1");
    assertThat(conditionFullView.getConditionDescription()).isEqualTo("CC description");
    assertThat(conditionFullView.isItemsAllowed()).isTrue();

    ControlCodeConditionFullView.ConditionDescriptionControlCodes codes = conditionFullView.getConditionDescriptionControlCodes();
    assertThat(codes.getControlCodes()).extracting(ControlCodeConditionFullView.ControlCode::getId).containsExactly("CCID1", "CCID2");
    assertThat(codes.getControlCodes()).extracting(ControlCodeConditionFullView.ControlCode::getControlCode).containsExactly("CC1", "CC2");
    assertThat(codes.getControlCodes()).extracting(ControlCodeConditionFullView.ControlCode::getFriendlyDescription).containsExactly("Friendly 1", "Friendly 2");
    assertThat(codes.getMissingControlCodes()).containsExactly("MISSING1", "MISSING2");
  }

}