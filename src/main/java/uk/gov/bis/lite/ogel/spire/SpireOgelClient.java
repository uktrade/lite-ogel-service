package uk.gov.bis.lite.ogel.spire;

import uk.gov.bis.lite.common.spire.client.SpireClient;
import uk.gov.bis.lite.common.spire.client.SpireClientConfig;
import uk.gov.bis.lite.common.spire.client.SpireRequestConfig;
import uk.gov.bis.lite.common.spire.client.parser.SpireParser;
import uk.gov.bis.lite.ogel.model.SpireOgel;

import java.util.List;

public class SpireOgelClient extends SpireClient<List<SpireOgel>> {

  public SpireOgelClient(SpireParser<List<SpireOgel>> parser,
                         SpireClientConfig clientConfig,
                         SpireRequestConfig requestConfig) {
    super(parser, clientConfig, requestConfig);
  }
}
