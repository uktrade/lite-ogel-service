package uk.gov.bis.lite.ogel.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.ogel.api.view.ValidateView;
import uk.gov.bis.lite.ogel.exception.CacheNotPopulatedException;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.local.ogel.LocalOgel;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;
import uk.gov.bis.lite.ogel.util.AuthUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AdminResourceTest {

  private final LocalOgelService localOgelService = mock(LocalOgelService.class);
  private final SpireOgelService spireOgelService = mock(SpireOgelService.class);

  @Rule
  public final ResourceTestRule resources = AuthUtil.authBuilder()
      .addResource(new ValidateResource(localOgelService, spireOgelService, "testUrl"))
      .addProvider(CacheNotPopulatedException.CacheNotPopulatedExceptionHandler.class)
      .build();

  @Test
  public void validateShouldReturnOkStatus() {
    when(localOgelService.getAllLocalOgels()).thenReturn(localOgels("OG1", "OG2", "OG3", "OG4"));
    when(spireOgelService.getAllOgels()).thenReturn(spireOgels("OG1", "OG2", "OG3", "OG4"));

    Response result = resources.client().target("/validate")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(200);
    ValidateView validateView = result.readEntity(ValidateView.class);
    assertThat(validateView).isNotNull();
    assertThat(validateView.getUnmatchedLocalOgelIds()).isEmpty();
    assertThat(validateView.getUnmatchedSpireOgelIds()).isEmpty();
  }

  @Test
  public void validateShouldReturnErrors() {
    when(localOgelService.getAllLocalOgels()).thenReturn(localOgels("OG1", "OG30", "OG31", "OG32"));
    when(spireOgelService.getAllOgels()).thenReturn(spireOgels("OG1", "OG2", "OG3", "OG4"));

    Response result = resources.client().target("/validate")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(500);
    ValidateView validateView = result.readEntity(ValidateView.class);
    assertThat(validateView).isNotNull();
    assertThat(validateView.getUnmatchedLocalOgelIds()).isEqualTo(Arrays.asList("OG30", "OG31", "OG32"));
    assertThat(validateView.getUnmatchedSpireOgelIds()).isEqualTo(Arrays.asList("OG2", "OG3", "OG4"));
  }

  @Test
  public void validateShouldReturnInternalServerErrorStatusForAnyErrors() {
    when(spireOgelService.getAllOgels()).thenThrow(new CacheNotPopulatedException(null));
    Response result = resources.client().target("/validate")
        .request(MediaType.APPLICATION_JSON)
        .header(AuthUtil.HEADER, AuthUtil.ADMIN_USER)
        .get();

    assertThat(result.getStatus()).isEqualTo(500);
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

}