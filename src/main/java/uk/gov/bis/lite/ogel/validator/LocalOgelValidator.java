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
    if (value == null) {
      return false;
    }
    if (value.getName() == null && value.getSummary() == null) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Invalid Local Ogel! Both Name and Summary fields are empty. " + value.getId())
          .addConstraintViolation();
      return false;
    }
    if (value.getSummary() != null &&
        (value.getSummary().getMustList() == null || value.getSummary().getCantList() == null
            || value.getSummary().getCanList() == null || value.getSummary().getHowToUseList() == null)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Invalid Local Ogel! Missing a summary field " + value.getId())
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
