package uk.gov.bis.lite.ogel.validator;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class LocalOgelValidator implements ConstraintValidator<CheckLocalOgel, LocalOgel> {
  @Override
  public void initialize(CheckLocalOgel constraintAnnotation) {
  }

  @Override
  public boolean isValid(LocalOgel value, ConstraintValidatorContext context) {
    StringBuilder errorMessage = new StringBuilder("Invalid Local Ogel! ");
    if (value == null) {
      return false;
    }
    if (value.getName() == null && value.getSummary() == null) {
      context.disableDefaultConstraintViolation();
      errorMessage.append("Both Name and Summary fields are empty. ");
      if (value.getId() != null) {
        errorMessage.append("Ogel ID " + value.getId());
      }
      context.buildConstraintViolationWithTemplate(errorMessage.toString())
          .addConstraintViolation();
      return false;
    }

    if (value.getSummary() != null) {
      boolean missingField = false;
      errorMessage.append("Missing Ogel Condition Summary Fields: ");
      if (value.getSummary().getMustList() == null) {
        errorMessage.append("mustList ");
        missingField = true;
      }
      if (value.getSummary().getCantList() == null) {
        errorMessage.append("cantList ");
        missingField = true;
      }
      if (value.getSummary().getCanList() == null) {
        errorMessage.append("canList ");
        missingField = true;
      }
      if (value.getSummary().getHowToUseList() == null) {
        errorMessage.append("howToUseList ");
        missingField = true;
      }
      if (missingField) {
        context.disableDefaultConstraintViolation();
        if (value.getId() != null) {
          errorMessage.append("Ogel ID" + value.getId());
        }
        context.buildConstraintViolationWithTemplate(errorMessage.toString())
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }
}
