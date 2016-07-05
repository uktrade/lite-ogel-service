package uk.gov.bis.lite.ogel.validator;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocalOgelValidator implements ConstraintValidator<CheckLocalOgel, LocalOgel> {
  @Override
  public void initialize(CheckLocalOgel constraintAnnotation) {
  }

  @Override
  public boolean isValid(LocalOgel value, ConstraintValidatorContext context) {
    if(value == null){
      return false;
    }
    if (value.getName() == null && value.getSummary() == null) {
      return false;
    }
    return !(value.getSummary() != null &&
        (value.getSummary().getMustList() == null || value.getSummary().getCantList() == null
            || value.getSummary().getCanList() == null || value.getSummary().getHowToUseList() == null));
  }
}
