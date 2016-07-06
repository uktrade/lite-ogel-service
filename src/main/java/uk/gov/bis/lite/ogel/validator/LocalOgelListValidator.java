package uk.gov.bis.lite.ogel.validator;

import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;
import java.util.stream.Collectors;

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
    LocalOgelValidator localOgelValidator = new LocalOgelValidator();
    List<LocalOgel> faultyLocalOgels = value.stream().filter(o -> !localOgelValidator.isValid(o, context)).collect(Collectors.toList());
    if (!faultyLocalOgels.isEmpty()) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("A faulty local ogel data found : " + faultyLocalOgels.get(0).getId())
          .addConstraintViolation();
      return false;
    }
    List<LocalOgel> duplicateIdOgels = value.stream().filter(lo ->
        value.stream().anyMatch(lo2 -> lo.getId().equalsIgnoreCase(lo2.getId()) && !lo.equals(lo2))
    ).collect(Collectors.toList());
    if (!duplicateIdOgels.isEmpty()) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("A duplicate ID found in bulk update data: " + duplicateIdOgels.get(0).getId())
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
