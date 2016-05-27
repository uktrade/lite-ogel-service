package uk.gov.bis.lite.ogel.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundException;
import uk.gov.bis.lite.ogel.database.exception.LocalOgelNotFoundExceptionHandler;
import uk.gov.bis.lite.ogel.database.exception.OgelNotFoundExceptionHandler;
import uk.gov.bis.lite.ogel.model.CategoryType;
import uk.gov.bis.lite.ogel.model.SpireOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.OgelSummary;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;

public class SpireMergedOgelViewResourceTest {

  private static final SpireOgelService ogelSpireService = Mockito.mock(SpireOgelService.class);
  private static final LocalOgelService ogelLocalService = Mockito.mock(LocalOgelService.class);

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new SpireMergedOgelViewResource(ogelSpireService, ogelLocalService))
      .addResource(new OgelNotFoundExceptionHandler())
      .addResource(new LocalOgelNotFoundExceptionHandler())
      .build();

  @Test
  public void getsExpectedSpireOgel() throws SOAPException, XPathExpressionException, UnsupportedEncodingException {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setId("OGL1");
    spireOgel.setCategory(CategoryType.REPAIR);
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId("OGL1");
    OgelSummary summary = new OgelSummary();
    summary.setCanList(Collections.singletonList("cando1, cando2, cando3"));
    summary.setCantList(Collections.singletonList("illegal1, illegal2"));
    summary.setMustList(Collections.singletonList("mustdo1, mustdo2"));
    localOgel.setSummary(summary);
    //spireOgel.setLocalOgel(localOgel);
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    final Response response = resources.client().target("/ogel/OGL1").request().get();
    final SpireOgel spireOgelFromResponse = response.readEntity(SpireOgel.class);
    assertNotNull(spireOgelFromResponse);
  }

  @Test
  public void OgelNotFoundExceptionIsHandled() {
    when(ogelSpireService.findSpireOgelById(anyList(), anyString())).thenReturn(null);
    final Response response = resources.client().target("/ogel/invalid").request().get();
    assertEquals(404, response.getStatus());
    assertEquals("No Ogel Found With Given Ogel ID: invalid", response.readEntity(String.class));
  }

  @Test
  public void LocalOgelNotFoundExceptionIsHandled() throws LocalOgelNotFoundException {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setId("OGL1");
    spireOgel.setCategory(CategoryType.REPAIR);
    when(ogelSpireService.findSpireOgelById(anyList(), anyString())).thenReturn(spireOgel);
    when(ogelLocalService.findLocalOgelById((anyString()))).thenReturn(null);
    final Response response = resources.client().target("/ogel/unknown").request().get();
    assertEquals(500, response.getStatus());
    assertEquals("An unexpected error occurred", response.readEntity(String.class));
  }
}
