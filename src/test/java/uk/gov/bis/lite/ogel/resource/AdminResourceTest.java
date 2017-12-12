package uk.gov.bis.lite.ogel.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.api.view.ValidateView;
import uk.gov.bis.lite.ogel.client.ControlCodeClient;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.resource.auth.SimpleAuthenticator;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AdminResourceTest {

  private final LocalOgelService localOgelService = mock(LocalOgelService.class);
  private final SpireOgelService spireOgelService = mock(SpireOgelService.class);
  private final LocalControlCodeConditionService controlCodeConditionService = mock(LocalControlCodeConditionService.class);
  private final ControlCodeClient controlCodeClient = mock(ControlCodeClient.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new AdminResource(localOgelService, spireOgelService, controlCodeConditionService, controlCodeClient, "testUrl"))
      .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
          .setAuthenticator(new SimpleAuthenticator("user", "password"))
          .setRealm("SUPER SECRET STUFF")
          .buildAuthFilter()))
      .addProvider(CacheNotPopulatedException.CacheNotPopulatedExceptionHandler.class)
      .addResource(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class))
      .build();

  @Test
  public void validateShouldReturnOkStatus() throws Exception {
    when(localOgelService.getAllLocalOgels()).thenReturn(localOgels("OG1", "OG2", "OG3", "OG4"));
    when(spireOgelService.getAllOgels()).thenReturn(spireOgels("OG1", "OG2", "OG3", "OG4"));
    when(controlCodeConditionService.getAllControlCodeConditions())
        .thenReturn(controlCodeConditions(ImmutableMap.of(
            "C1", new ArrayList<>(),
            "C2", new ArrayList<>(),
            "C3", Collections.singletonList("C1"),
            "C4", new ArrayList<>())));
    when(controlCodeClient.getAllControlCodes()).thenReturn(controlCodes("C1", "C2", "C3", "C4"));

    Response result = resources.getJerseyTest().target("/admin/validate")
        .request(MediaType.APPLICATION_JSON)
        .header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
        .get();

    assertThat(result.getStatus()).isEqualTo(200);
    ValidateView validateView = result.readEntity(ValidateView.class);
    assertThat(validateView).isNotNull();
    assertThat(validateView.getUnmatchedControlCodes()).isEmpty();
    assertThat(validateView.getUnmatchedLocalOgelIds()).isEmpty();
    assertThat(validateView.getUnmatchedSpireOgelIds()).isEmpty();
  }

  @Test
  public void validateShouldReturnErrors() throws Exception {
    when(localOgelService.getAllLocalOgels()).thenReturn(localOgels("OG1", "OG30", "OG31", "OG32"));
    when(spireOgelService.getAllOgels()).thenReturn(spireOgels("OG1", "OG2", "OG3", "OG4"));
    when(controlCodeConditionService.getAllControlCodeConditions())
        .thenReturn(controlCodeConditions(ImmutableMap.of(
            "C1", new ArrayList<>(),
            "C2", new ArrayList<>(),
            "C3", Arrays.asList("C1", "C100"),
            "C4", new ArrayList<>())));
    when(controlCodeClient.getAllControlCodes()).thenReturn(controlCodes("C1", "C2", "C3", "C4"));

    Response result = resources.getJerseyTest().target("/admin/validate")
        .request(MediaType.APPLICATION_JSON)
        .header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
        .get();

    assertThat(result.getStatus()).isEqualTo(500);
    ValidateView validateView = result.readEntity(ValidateView.class);
    assertThat(validateView).isNotNull();
    assertThat(validateView.getUnmatchedControlCodes()).isEqualTo(ImmutableMap.of("OG1", Collections.singletonList("C100")));
    assertThat(validateView.getUnmatchedLocalOgelIds()).isEqualTo(Arrays.asList("OG30", "OG31", "OG32"));
    assertThat(validateView.getUnmatchedSpireOgelIds()).isEqualTo(Arrays.asList("OG2", "OG3", "OG4"));
  }

  @Test
  public void validateShouldReturnInternalServerErrorStatusForAnyErrors() throws Exception {

    when(spireOgelService.getAllOgels()).thenThrow(new CacheNotPopulatedException(null));
    Response result = resources.getJerseyTest().target("/admin/validate")
        .request(MediaType.APPLICATION_JSON)
        .header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
        .get();

    assertThat(result.getStatus()).isEqualTo(500);
  }

  @Test
  public void validateShouldReturnUnauthorisedStatus() throws Exception {

    Response result = resources.getJerseyTest().target("/admin/validate")
        .request(MediaType.APPLICATION_JSON)
        .header("Authorization", "blah")
        .get();

    assertThat(result.getStatus()).isEqualTo(401);
  }

  private List<LocalOgel> localOgels(String... ids) {
    return Arrays.stream(ids).map(i -> {
      LocalOgel localOgel = new LocalOgel();
      localOgel.setId(i);
      return localOgel;
    }).collect(Collectors.toList());
  }

  private List<SpireOgel> spireOgels(String... ids) {
    return Arrays.stream(ids).map(i -> {
      SpireOgel spireOgel = new SpireOgel();
      spireOgel.setId(i);
      return spireOgel;
    }).collect(Collectors.toList());
  }

  private List<LocalControlCodeCondition> controlCodeConditions(Map<String, List<String>> controlCodes) {
    return controlCodes.entrySet().stream().map(i -> {
      LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
      controlCodeCondition.setOgelID("OG1");
      controlCodeCondition.setControlCode(i.getKey());
      controlCodeCondition.setConditionDescriptionControlCodes(i.getValue());
      return controlCodeCondition;
    }).collect(Collectors.toList());
  }

  private List<ControlCodeFullView> controlCodes(String... controlCodes) {
    return Arrays.stream(controlCodes).map(c -> {
      ControlCodeFullView controlCodeFullView = new ControlCodeFullView();
      controlCodeFullView.setControlCode(c);
      return controlCodeFullView;
    }).collect(Collectors.toList());
  }
}