package uk.gov.bis.lite.ogel.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.bis.lite.ogel.model.localOgel.OgelConditionSummary;
import uk.gov.bis.lite.ogel.service.LocalOgelService;
import uk.gov.bis.lite.ogel.service.SpireOgelService;

import java.io.IOException;
import java.util.Arrays;
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
  public void getsExpectedSpireOgel() throws SOAPException, XPathExpressionException, IOException {
    SpireOgel spireOgel = new SpireOgel();
    spireOgel.setId("OGL1");
    spireOgel.setCategory(CategoryType.REPAIR);
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId("OGL1");
    OgelConditionSummary summary = new OgelConditionSummary();
    summary.setCanList(Arrays.asList("cando1", "cando2", "cando3"));
    summary.setCantList(Arrays.asList("illegal1", "illegal2"));
    summary.setMustList(Arrays.asList("mustdo1", "mustdo2"));
    summary.setHowToUseList(Arrays.asList("howtoUse1", "howtoUse2"));
    localOgel.setSummary(summary);
    when(ogelSpireService.getAllOgels()).thenReturn(Collections.singletonList(spireOgel));
    when(ogelSpireService.findSpireOgelById(anyList(), anyString())).thenCallRealMethod();
    when(ogelLocalService.findLocalOgelById(anyString())).thenReturn(localOgel);
    final Response response = resources.client().target("/ogel/OGL1").request().get();
    ObjectMapper objectMapper = new ObjectMapper();
    //jackson won't auto deserialize the custom OgelFullView object
    final JsonNode responseJsonNode = objectMapper.readTree(response.readEntity(String.class));
    assertEquals("OGL1", responseJsonNode.get("id").asText());
    assertEquals(3, responseJsonNode.get("summary").get("canList").size());
    assertEquals("cando2", responseJsonNode.get("summary").get("canList").get(1).asText());
    assertEquals("illegal1", responseJsonNode.get("summary").get("cantList").get(0).asText());
    assertEquals(2, responseJsonNode.get("summary").get("mustList").size());

  }

  @Test
  public void OgelNotFoundExceptionIsHandled() {
    when(ogelSpireService.findSpireOgelById(anyList(), anyString())).thenCallRealMethod();
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
    when(ogelLocalService.findLocalOgelById((anyString()))).thenThrow(new LocalOgelNotFoundException("unknown"));
    final Response response = resources.client().target("/ogel/unknown").request().get();
    assertEquals(500, response.getStatus());
    assertEquals("An unexpected error occurred. Failed to find local OGEL entry with ID: unknown", response.readEntity(String.class));
  }
}
