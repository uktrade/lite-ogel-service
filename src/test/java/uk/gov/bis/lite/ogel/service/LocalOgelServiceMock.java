package uk.gov.bis.lite.ogel.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgelConditionSummary;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class LocalOgelServiceMock implements LocalOgelService {

  private final LocalOgel localOgel;

  private boolean missingLocalOgel;

  @Inject
  public LocalOgelServiceMock() {
    localOgel = buildLocalOgel("OGL1");
    missingLocalOgel = false;
  }

  @Override
  public LocalOgel updateSpireOgelCondition(String ogelID, List<String> newConditionList, String conditionField) {
    return null;
  }

  @Override
  public Optional<LocalOgel> findLocalOgelById(String id) {
    if (missingLocalOgel) {
      return Optional.empty();
    } else {
      return Optional.of(localOgel);
    }
  }

  @Override
  public LocalOgel insertOrUpdateOgel(LocalOgel ogel) {
    return null;
  }

  @Override
  public void insertOgelList(List<LocalOgel> ogelList) {

  }

  @Override
  public List<LocalOgel> getAllLocalOgels() {
    if (missingLocalOgel) {
      return Collections.emptyList();
    } else {
      return Collections.singletonList(localOgel);
    }
  }

  @Override
  public void deleteAllOgels() {

  }

  @Override
  public void deleteOgelById(String ogelId) {

  }

  private LocalOgel buildLocalOgel(String id) {
    LocalOgel localOgel = new LocalOgel();
    localOgel.setId(id);
    localOgel.setName("name");
    LocalOgelConditionSummary summary = new LocalOgelConditionSummary();
    summary.setCanList(Collections.singletonList("can"));
    summary.setCantList(Collections.singletonList("can't"));
    summary.setMustList(Collections.singletonList("must"));
    summary.setHowToUseList(Collections.singletonList("how to use"));
    localOgel.setSummary(summary);
    return localOgel;
  }

  public void setMissingLocalOgel(boolean missingLocalOgel) {
    this.missingLocalOgel = missingLocalOgel;
  }
}
