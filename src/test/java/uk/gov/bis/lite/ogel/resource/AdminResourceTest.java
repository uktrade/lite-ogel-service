package uk.gov.bis.lite.ogel.resource;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.ogel.model.ControlCodeFullView;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.ValidateView;
import uk.gov.bis.lite.ogel.model.localOgel.LocalControlCodeCondition;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.resource.auth.SimpleAuthenticator;
import uk.gov.bis.lite.ogel.service.LocalControlCodeConditionService;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminResourceTest {

  private final LocalOgelService localOgelService = mock(LocalOgelService.class);
  private final SpireOgelService spireOgelService = mock(SpireOgelService.class);
  private final LocalControlCodeConditionService controlCodeConditionService = mock(LocalControlCodeConditionService.class);
  private final Client client = mock(Client.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
    .addResource(new AdminResource(localOgelService, spireOgelService, controlCodeConditionService, "someUrl", client))
    .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
      .setAuthenticator(new SimpleAuthenticator("user", "password"))
      .setRealm("SUPER SECRET STUFF")
      .buildAuthFilter()))
    .addResource(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class))
    .build();

  @Test
  public void shouldGetValidate() throws Exception {
    when(localOgelService.getAllLocalOgels()).thenReturn(localOgels("OG1", "OG30", "OG31", "OG32"));
    when(spireOgelService.getAllOgels()).thenReturn(spireOgels("OG1", "OG2", "OG3", "OG4"));
    when(controlCodeConditionService.getAllControlCodeConditions())
      .thenReturn(controlCodeConditions("OG1", ImmutableMap.of(
        "C1", new ArrayList<>(),
        "C2", new ArrayList<>(),
        "C3", Arrays.asList("C1", "C100"),
        "C4", new ArrayList<>())));
    WebTarget webTarget = mock(WebTarget.class);
    WebTarget webTarget2 = mock(WebTarget.class);
    Invocation.Builder builder = mock(Invocation.Builder.class);
    when(webTarget2.request()).thenReturn(builder);
    when(webTarget.path("/control-codes")).thenReturn(webTarget2);
    when(client.target("someUrl")).thenReturn(webTarget);
    Response response = mock(Response.class);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.readEntity(any(GenericType.class))).thenReturn(controlCodes("C1", "C2", "C1", "C4"));
    when(builder.get()).thenReturn(response);


    Response result  = resources.getJerseyTest().target("/admin/validate")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
      .get();

    assertThat(result.getStatus()).isEqualTo(200);
    ValidateView validateView = result.readEntity(ValidateView.class);
    assertThat(validateView).isNotNull();
    assertThat(validateView.getUnmatchedControlCodes()).isEqualTo(ImmutableMap.of("OG1", Arrays.asList("C3", "C100")));
    assertThat(validateView.getUnmatchedLocalOgelIds()).isEqualTo(Arrays.asList("OG30", "OG31", "OG32"));
    assertThat(validateView.getUnmatchedSpireOgelIds()).isEqualTo(Arrays.asList("OG2", "OG3", "OG4"));
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

  private List<LocalControlCodeCondition> controlCodeConditions(String ogelId, Map<String,List<String>> controlCodes) {
    return controlCodes.entrySet().stream().map(i -> {
      LocalControlCodeCondition controlCodeCondition = new LocalControlCodeCondition();
      controlCodeCondition.setOgelID(ogelId);
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