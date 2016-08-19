package uk.gov.bis.lite.ogel.model;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

//Class serialised by Jackson
public class ApplicableOgelView {

  private final OgelFullView ogelFullView;

  public static ApplicableOgelView create(SpireOgel spireOgel, LocalOgel localOgel) {
    return new ApplicableOgelView(new OgelFullView(spireOgel, localOgel));
  }

  public ApplicableOgelView(OgelFullView ogelFullView) {
    this.ogelFullView = ogelFullView;
  }

  public String getId() {
    return ogelFullView.getOgelId();
  }

  public String getName() {
    return ogelFullView.getOgelName();
  }

  public List<String> getUsageSummary() {
    return ogelFullView.getSummary().getCanList();
  }
}
