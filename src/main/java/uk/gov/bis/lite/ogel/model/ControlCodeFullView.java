package uk.gov.bis.lite.ogel.model;

import java.util.List;

public class ControlCodeFullView {

  private String id;
  private String parentId;
  private String controlCode;
  private String category;
  private String friendlyDescription;
  private String legalDescription;
  private Boolean selectable;
  private String revisionDate;
  private String lastModifiedInRevision;
  private String beforeLegalDefinitionText;
  private String afterLegalDefinitionText;
  private String displayOrder;
  private String reasonForControl;
  private List<String> decontrols;
  private String definitionOfTerms;
  private String title;
  private String technicalNotes;
  private Boolean isShowInHierarchy;
  private AdditionalSpecifications additionalSpecifications;
  private String alias;

  public void setId(String id) {
    this.id = id;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public void setControlCode(String controlCode) {
    this.controlCode = controlCode;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setFriendlyDescription(String friendlyDescription) {
    this.friendlyDescription = friendlyDescription;
  }

  public void setLegalDescription(String legalDescription) {
    this.legalDescription = legalDescription;
  }

  public void setSelectable(Boolean selectable) {
    this.selectable = selectable;
  }

  public void setRevisionDate(String revisionDate) {
    this.revisionDate = revisionDate;
  }

  public void setLastModifiedInRevision(String lastModifiedInRevision) {
    this.lastModifiedInRevision = lastModifiedInRevision;
  }

  public void setBeforeLegalDefinitionText(String beforeLegalDefinitionText) {
    this.beforeLegalDefinitionText = beforeLegalDefinitionText;
  }

  public void setAfterLegalDefinitionText(String afterLegalDefinitionText) {
    this.afterLegalDefinitionText = afterLegalDefinitionText;
  }

  public void setDisplayOrder(String displayOrder) {
    this.displayOrder = displayOrder;
  }

  public void setReasonForControl(String reasonForControl) {
    this.reasonForControl = reasonForControl;
  }

  public void setDecontrols(List<String> decontrols) {
    this.decontrols = decontrols;
  }

  public void setDefinitionOfTerms(String definitionOfTerms) {
    this.definitionOfTerms = definitionOfTerms;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTechnicalNotes(String technicalNotes) {
    this.technicalNotes = technicalNotes;
  }

  public void setShowInHierarchy(Boolean showInHierarchy) {
    isShowInHierarchy = showInHierarchy;
  }

  public void setAdditionalSpecifications(AdditionalSpecifications additionalSpecifications) {
    this.additionalSpecifications = additionalSpecifications;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getId() {
    return id;
  }

  public String getParentId() {
    return parentId;
  }

  public String getControlCode() {
    return controlCode;
  }

  public String getCategory() {
    return category;
  }

  public String getFriendlyDescription() {
    return friendlyDescription;
  }

  public String getLegalDescription() {
    return legalDescription;
  }

  public Boolean getSelectable() {
    return selectable;
  }

  public String getRevisionDate() {
    return revisionDate;
  }

  public String getLastModifiedInRevision() {
    return lastModifiedInRevision;
  }

  public String getBeforeLegalDefinitionText() {
    return beforeLegalDefinitionText;
  }

  public String getAfterLegalDefinitionText() {
    return afterLegalDefinitionText;
  }

  public String getDisplayOrder() {
    return displayOrder;
  }

  public String getReasonForControl() {
    return reasonForControl;
  }

  public List<String> getDecontrols() {
    return decontrols;
  }

  public String getDefinitionOfTerms() {
    return definitionOfTerms;
  }

  public String getTitle() {
    return title;
  }

  public String getTechnicalNotes() {
    return technicalNotes;
  }

  public Boolean getShowInHierarchy() {
    return isShowInHierarchy;
  }

  public AdditionalSpecifications getAdditionalSpecifications() {
    return additionalSpecifications;
  }

  public String getAlias() {
    return alias;
  }
}
