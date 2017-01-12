package uk.gov.bis.lite.ogel.factory;

import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.ogel.api.view.ApplicableOgelView;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.ogel.model.BulkControlCodeCutDowns;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewFactory {

  public static ControlCodeConditionFullView createControlCodeCondition(LocalControlCodeCondition localControlCodeCondition) {
    ControlCodeConditionFullView view = new ControlCodeConditionFullView();
    view.setOgelId(localControlCodeCondition.getOgelID());
    view.setControlCode(localControlCodeCondition.getControlCode());
    view.setConditionDescription(localControlCodeCondition.getConditionDescription());
    view.setItemsAllowed(localControlCodeCondition.isItemsAllowed());
    return view;
  }

  public static ControlCodeConditionFullView createControlCodeCondition(LocalControlCodeCondition localControlCodeCondition, BulkControlCodeCutDowns bulkControlCodeCutDowns) {
    ControlCodeConditionFullView view = createControlCodeCondition(localControlCodeCondition);
    ControlCodeConditionFullView.ConditionDescriptionControlCodes conditionDescriptionControlCodes = new ControlCodeConditionFullView.ConditionDescriptionControlCodes();
    List<ControlCodeConditionFullView.ControlCode> controlCodeCutDowns = bulkControlCodeCutDowns.getControlCodes().stream()
      .map(c -> {
        ControlCodeConditionFullView.ControlCode controlCodeCutDown = new ControlCodeConditionFullView.ControlCode();
        controlCodeCutDown.setId(c.getId());
        controlCodeCutDown.setControlCode(c.getControlCode());
        controlCodeCutDown.setFriendlyDescription(c.getFriendlyDescription());
        return controlCodeCutDown;
      })
      .collect(Collectors.toList());
    conditionDescriptionControlCodes.setControlCodes(controlCodeCutDowns);
    conditionDescriptionControlCodes.setMissingControlCodes(bulkControlCodeCutDowns.getMissingControlCodes());
    view.setConditionDescriptionControlCodes(conditionDescriptionControlCodes);
    return view;
  }

  public static OgelFullView createOgel(SpireOgel spireOgel, LocalOgel localOgel) {
    OgelFullView ogelFullView = new OgelFullView();
    ogelFullView.setId(spireOgel.getId());
    ogelFullView.setName(getOgelName(localOgel, spireOgel));

    OgelFullView.OgelConditionSummary summary = new OgelFullView.OgelConditionSummary();
    if (localOgel != null) {
      summary.setCanList(localOgel.getSummary().getCanList());
      summary.setCantList(localOgel.getSummary().getCantList());
      summary.setMustList(localOgel.getSummary().getMustList());
      summary.setHowToUseList(localOgel.getSummary().getHowToUseList());
    } else {
      summary.setCanList(new ArrayList<>());
      summary.setCantList(new ArrayList<>());
      summary.setMustList(new ArrayList<>());
      summary.setHowToUseList(new ArrayList<>());
    }
    ogelFullView.setSummary(summary);
    return ogelFullView;
  }

  public static ApplicableOgelView createApplicableOgel(SpireOgel spireOgel, LocalOgel localOgel) {
    ApplicableOgelView applicableOgelView = new ApplicableOgelView();
    applicableOgelView.setId(spireOgel.getId());
    applicableOgelView.setName(getOgelName(localOgel, spireOgel));
    if (localOgel != null) {
      applicableOgelView.setUsageSummary(localOgel.getSummary().getCanList());
    } else {
      applicableOgelView.setUsageSummary(new ArrayList<>());
    }
    return applicableOgelView;
  }

  private static String getOgelName(LocalOgel localOgel, SpireOgel spireOgel) {
    if (localOgel == null || StringUtils.isBlank(localOgel.getName())) {
      return spireOgel.getName();
    } else {
      return localOgel.getName();
    }
  }
}