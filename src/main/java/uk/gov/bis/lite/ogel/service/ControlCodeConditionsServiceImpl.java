package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.client.ControlCodeClient;
import uk.gov.bis.lite.ogel.factory.ViewFactory;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;

import java.util.List;

import javax.annotation.Nullable;

@Singleton
public class ControlCodeConditionsServiceImpl implements ControlCodeConditionsService {
  private final LocalControlCodeConditionService localControlCodeConditionService;
  private final ControlCodeClient controlCodeClient;

  @Inject
  public ControlCodeConditionsServiceImpl(LocalControlCodeConditionService localControlCodeConditionService, ControlCodeClient controlCodeClient) {
    this.localControlCodeConditionService = localControlCodeConditionService;
    this.controlCodeClient = controlCodeClient;
  }

  @Nullable
  @Override
  public ControlCodeConditionFullView findControlCodeConditions(String ogelID, String controlCode) {
    LocalControlCodeCondition localConditions = localControlCodeConditionService.getLocalControlCodeConditionsByIdAndControlCode(ogelID, controlCode);
    if (localConditions == null) {
      return null;
    }
    else {
      List<String> controlCodes = localConditions.getConditionDescriptionControlCodes();
      if (!controlCodes.isEmpty()) {
        BulkControlCodes bulkControlCodes = controlCodeClient.bulkControlCodes(controlCodes);
        return ViewFactory.createControlCodeCondition(localConditions, bulkControlCodes);
      }
      else {
        return ViewFactory.createControlCodeCondition(localConditions);
      }
    }
  }
}
