package uk.gov.bis.lite.ogel.validator;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocalOgelListValidator implements ConstraintValidator<CheckLocalOgelList, List<LocalOgel>> {
  @Override
  public void initialize(CheckLocalOgelList constraintAnnotation) {
  }

  @Override
  public boolean isValid(List<LocalOgel> value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    long duplicateIdCount = value.stream().filter(lo ->
        value.stream().anyMatch(lo2 -> lo.getId().equalsIgnoreCase(lo2.getId()) && !lo.equals(lo2))
    ).count();
    return duplicateIdCount <= 0;
  }
}
