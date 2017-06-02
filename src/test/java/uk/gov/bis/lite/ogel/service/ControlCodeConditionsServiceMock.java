package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.factory.ViewFactory;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.Collections;
import java.util.Optional;

@Singleton
public class ControlCodeConditionsServiceMock implements ControlCodeConditionsService {

  private boolean conditionsFound;
  private boolean controlCodeDescriptionsFound;
  private boolean controlCodeDescriptionsMissingControlCodes;

  @Inject
  public ControlCodeConditionsServiceMock() {
    conditionsFound = true;
  }

  @Override
  public Optional<ControlCodeConditionFullView> findControlCodeConditions(String ogelID, String controlCode) {
    if (conditionsFound) {
      return Optional.of(buildControlCodeConditionFullView());
    } else {
      return Optional.empty();
    }
  }

  private ControlCodeConditionFullView buildControlCodeConditionFullView() {
    LocalControlCodeCondition condition = new LocalControlCodeCondition();
    condition.setOgelID("OGL1");
    condition.setControlCode("ML1a");
    condition.setItemsAllowed(false);
    condition.setConditionDescription("<p>Fully automatic weapons</p>");
    if (controlCodeDescriptionsFound) {
      BulkControlCodes bulkControlCodes = new BulkControlCodes();
      if (controlCodeDescriptionsMissingControlCodes) {
        bulkControlCodes.setMissingControlCodes(Collections.singletonList("ML1a"));
        bulkControlCodes.setControlCodeFullViews(Collections.emptyList());
      } else {
        ControlCodeFullView controlCode = new ControlCodeFullView();
        controlCode.setId("ML1a");
        controlCode.setFriendlyDescription("Rifles and combination guns, handguns, machine, sub-machine and volley guns");
        controlCode.setControlCode("ML1a");
        bulkControlCodes.setControlCodeFullViews(Collections.singletonList(controlCode));
        bulkControlCodes.setMissingControlCodes(Collections.emptyList());
      }
      return ViewFactory.createControlCodeCondition(condition, bulkControlCodes);
    } else {
      return ViewFactory.createControlCodeCondition(condition);
    }
  }

  public ControlCodeConditionsServiceMock setConditionsFound(boolean conditionsFound) {
    this.conditionsFound = conditionsFound;
    return this;
  }

  public ControlCodeConditionsServiceMock setControlCodeDescriptionsFound(boolean controlCodeDescriptionsFound) {
    this.controlCodeDescriptionsFound = controlCodeDescriptionsFound;
    return this;
  }

  public ControlCodeConditionsServiceMock setControlCodeDescriptionsMissingControlCodes(boolean controlCodeDescriptionsMissingControlCodes) {
    this.controlCodeDescriptionsMissingControlCodes = controlCodeDescriptionsMissingControlCodes;
    return this;
  }
}
