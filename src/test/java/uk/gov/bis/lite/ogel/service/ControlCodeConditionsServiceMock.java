package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView.ConditionDescriptionControlCodes;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView.ControlCode;

import java.util.Collections;

import javax.annotation.Nullable;

@Singleton
public class ControlCodeConditionsServiceMock implements ControlCodeConditionsService {

  private final Logger LOGGER = LoggerFactory.getLogger(ControlCodeConditionsServiceMock.class);

  private boolean conditionsFound;
  private boolean controlCodeDescriptionsFound;
  private boolean controlCodeDescriptionsMissingControlCodes;

  @Inject
  public ControlCodeConditionsServiceMock() {
    conditionsFound = true;
  }

  @Nullable
  @Override
  public ControlCodeConditionFullView findControlCodeConditions(String ogelID, String controlCode) {
    LOGGER.debug("" + conditionsFound);
    return conditionsFound ? buildControlCodeConditionFullView() : null;
  }

  private ControlCodeConditionFullView buildControlCodeConditionFullView() {
    ControlCodeConditionFullView view = new ControlCodeConditionFullView();
    view.setOgelId("OGL1");
    view.setControlCode("ML1a");
    view.setItemsAllowed(false);
    view.setConditionDescription("<p>Fully automatic weapons</p>");
    if (controlCodeDescriptionsFound) {
      ConditionDescriptionControlCodes descriptionControlCodes = new ConditionDescriptionControlCodes();
      if (controlCodeDescriptionsMissingControlCodes) {
        descriptionControlCodes.setControlCodes(Collections.emptyList());
        descriptionControlCodes.setMissingControlCodes(Collections.singletonList("ML1a"));
      } else {
        ControlCode controlCode = new ControlCode();
        controlCode.setId("ML1a");
        controlCode.setFriendlyDescription("Rifles and combination guns, handguns, machine, sub-machine and volley guns");
        controlCode.setControlCode("ML1a");
        descriptionControlCodes.setControlCodes(Collections.singletonList(controlCode));
        descriptionControlCodes.setMissingControlCodes(Collections.emptyList());
      }
      view.setConditionDescriptionControlCodes(descriptionControlCodes);
    }
    return view;
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
