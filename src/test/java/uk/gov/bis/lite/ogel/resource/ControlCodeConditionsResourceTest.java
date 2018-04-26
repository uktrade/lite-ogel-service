package uk.gov.bis.lite.ogel.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.controlcode.api.view.BulkControlCodes;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.api.view.ControlCodeConditionFullView;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.factory.ViewFactory;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;
import uk.gov.bis.lite.ogel.service.ControlCodeConditionsService;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ControlCodeConditionsResourceTest {

  private static final String CONTROL_CODE = "ML1a";
  private static final String OGEL_ID = "OGL01";

  private final SpireOgelService spireOgelService = mock(SpireOgelService.class);
  private final LocalOgelService localOgelService = mock(LocalOgelService.class);
  private final LocalControlCodeConditionService localControlCodeConditionService = mock(LocalControlCodeConditionService.class);
  private final ControlCodeConditionsService controlCodeConditionsService = mock(ControlCodeConditionsService.class);

  @Rule
  public final ResourceTestRule resources = AuthUtil.authBuilder()
      .addResource(new ControlCodeConditionsResource(spireOgelService, localOgelService, localControlCodeConditionService, controlCodeConditionsService))
      .addProvider(CacheNotPopulatedException.CacheNotPopulatedExceptionHandler.class)
      .build();

  @Test
  public void getControlCodeConditions() throws Exception {
    LocalControlCodeCondition controlCodeCondition = buildControlCodeCondition();
    when(localControlCodeConditionService.getAllControlCodeConditions()).thenReturn(Collections.singletonList(controlCodeCondition));

    Response result = resources.client().target("/control-code-conditions")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(200);
    List<LocalControlCodeCondition> controlCodeConditions = result.readEntity(new GenericType<List<LocalControlCodeCondition>>() {
    });
    assertThat(controlCodeConditions.size()).isEqualTo(1);
    assertThat(controlCodeConditions.get(0).getOgelID()).isEqualTo(OGEL_ID);
  }

  @Test
  public void getControlCodeConditionById() throws Exception {
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId(OGEL_ID);
    LocalControlCodeCondition controlCodeCondition = buildControlCodeCondition(new ArrayList<>());
    ControlCodeConditionFullView controlCodeConditionFullView = ViewFactory.createControlCodeCondition(controlCodeCondition);
    when(controlCodeConditionsService.findControlCodeConditions(OGEL_ID, CONTROL_CODE))
        .thenReturn(Optional.of(controlCodeConditionFullView));
    when(localOgelService.findLocalOgelById(anyString())).thenReturn(Optional.of(localOgel));

    Response result = resources.client().target("/control-code-conditions/OGL01/ML1a")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(200);
    String expectedJson = "{\"controlCode\":ML1a,\"conditionDescription\":Goods,\"itemsAllowed\":false,\"ogelId\":\"OGL01\",\"conditionDescriptionControlCodes\":null}";
    String actualStr = result.readEntity(String.class);
    assertEquals(expectedJson, actualStr, false);
  }

  @Test
  public void getBulkControlCodeConditionsById() throws Exception {
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId(OGEL_ID);
    when(localOgelService.findLocalOgelById(OGEL_ID)).thenReturn(Optional.of(localOgel));

    LocalControlCodeCondition controlCodeCondition = buildControlCodeCondition(Arrays.asList("ML1a", "ML1b"));
    ControlCodeFullView controlCodeFullView = new ControlCodeFullView();
    controlCodeFullView.setId("123");
    controlCodeFullView.setControlCode(CONTROL_CODE);
    controlCodeFullView.setFriendlyDescription("Goods");
    BulkControlCodes bulkControlCodes = new BulkControlCodes();
    bulkControlCodes.setControlCodeFullViews(Collections.singletonList(controlCodeFullView));
    bulkControlCodes.setMissingControlCodes(new ArrayList<>());
    ControlCodeConditionFullView controlCodeConditionFullView = ViewFactory.createControlCodeCondition(controlCodeCondition, bulkControlCodes);
    when(controlCodeConditionsService.findControlCodeConditions(OGEL_ID, CONTROL_CODE))
        .thenReturn(Optional.of(controlCodeConditionFullView));

    Response result = resources.client().target("/control-code-conditions/OGL01/ML1a")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(200);
    String expectedJson = "{\"controlCode\":\"ML1a\",\"conditionDescription\":\"Goods\",\"conditionDescriptionControlCodes\":{\"missingControlCodes\":[],\"controlCodes\":[{\"id\":\"123\",\"controlCode\":\"ML1a\",\"friendlyDescription\":\"Goods\"}]},\"itemsAllowed\":false,\"ogelId\":\"OGL01\"}";
    String actualStr = result.readEntity(String.class);
    assertEquals(expectedJson, actualStr, false);
  }

  @Test
  public void controlCodeConditionByIdReturnsNoContent() throws Exception {
    when(controlCodeConditionsService.findControlCodeConditions(OGEL_ID, CONTROL_CODE))
        .thenReturn(Optional.empty());
    when(localOgelService.findLocalOgelById(anyString())).thenReturn(Optional.empty());

    Response result = resources.client().target("/control-code-conditions/OGL01/ML1a")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(204);
  }

  @Test
  public void putOgelConditionsArraySuccess() {
    SpireOgel ogel = new SpireOgel();
    ogel.setId(OGEL_ID);
    when(spireOgelService.findSpireOgelById(OGEL_ID)).thenReturn(Optional.of(ogel));

    LocalControlCodeCondition controlCodeCondition = buildControlCodeCondition();

    Response result = resources.client().target("/control-code-conditions")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(Collections.singletonList(controlCodeCondition)));

    assertThat(result.getStatus()).isEqualTo(201);
  }

  @Test
  public void putOgelConditionsArrayOgelNotFound() {
    SpireOgel ogel = new SpireOgel();
    ogel.setId(OGEL_ID);
    when(spireOgelService.findSpireOgelById(anyString())).thenReturn(Optional.empty());

    LocalControlCodeCondition controlCodeCondition = buildControlCodeCondition();

    Response result = resources.client().target("/control-code-conditions")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .put(Entity.json(Collections.singletonList(controlCodeCondition)));

    assertThat(result.getStatus()).isEqualTo(404);
  }

  @Test
  public void deleteControlCodeConditions() throws Exception {
    Response result = resources.client().target("/control-code-conditions")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .delete();

    assertThat(result.getStatus()).isEqualTo(204);
    verify(localControlCodeConditionService).deleteControlCodeConditions();
  }

  private LocalControlCodeCondition buildControlCodeCondition() {
    return buildControlCodeCondition(new ArrayList<>());
  }

  private LocalControlCodeCondition buildControlCodeCondition(List<String> descriptions) {
    LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
    controlCodeCondition.setOgelID(OGEL_ID);
    controlCodeCondition.setControlCode(CONTROL_CODE);
    controlCodeCondition.setConditionDescription("Goods");
    controlCodeCondition.setConditionDescriptionControlCodes(descriptions);
    return controlCodeCondition;
  }
}
