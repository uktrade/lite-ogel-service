package uk.gov.bis.lite.ogel.pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.controlcode.api.view.ControlCodeFullView;
import uk.gov.bis.lite.ogel.client.ControlCodeClient;

import java.util.List;

import javax.ws.rs.client.ClientBuilder;

public class ControlCodePactTest {

  private ControlCodeClient controlCodeClient;

  private final static String PROVIDER = "lite-control-code-service";
  private final static String CONSUMER = "lite-ogel-service";

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void before() {
    controlCodeClient = new ControlCodeClient(ClientBuilder.newClient(), mockProvider.getConfig().url());
  }

  @Pact(provider = PROVIDER, consumer=CONSUMER)
  public PactFragment getAllControlCodesSuccess(PactDslWithProvider builder) {

    return builder
        .uponReceiving("request to get all control codes")
        .path("/control-codes")
        .method("GET")
        .willRespondWith()
          .status(200)
          .body(controlCodeFullViewPactDsl())
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "getAllControlCodesSuccess")
  public void shouldGetAllControlCodes() throws Exception {
    List<ControlCodeFullView> result = controlCodeClient.getAllControlCodes();
    assertThat(controlCodeClient.getAllControlCodes()).isNotEmpty();
  }

  private DslPart controlCodeFullViewPactDsl() {
      return PactDslJsonArray.arrayMinLike(1)
          .stringType("id")
          .stringType("parentId")
          .stringType("controlCode")
          .stringType("title")
          .stringType("technicalNotes")
          .stringType("alias")
          .stringType("category")
          .stringType("friendlyDescription")
          .stringType("legalDescription")
          .minArrayLike("decontrols", 0, PactDslJsonRootValue.stringType())
          .object("additionalSpecifications")
            .stringType("clauseText")
            .minArrayLike("specificationText", 0, PactDslJsonRootValue.stringType())
            .minArrayLike("specificationControlCodes", 0, PactDslJsonRootValue.stringType())
          .closeObject().asBody()
          .booleanType("selectable")
          .booleanType("showInHierarchy")
          .stringType("revisionDate")
          .stringType("lastModifiedInRevision")
          .stringType("beforeLegalDefinitionText")
          .stringType("afterLegalDefinitionText")
          .stringType("displayOrder")
          .stringType("reasonForControl")
          .stringType("definitionOfTerms")
        .closeObject();
  }
}
