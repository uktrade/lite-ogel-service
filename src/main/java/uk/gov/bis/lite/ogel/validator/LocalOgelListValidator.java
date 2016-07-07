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
    if (value.stream().filter(lo -> lo.getId() == null).findAny().isPresent()) {
      getCustomizedErrorMessage(context, "Local Ogel Without ID is not allowed!");
      return false;
    }
    LocalOgelValidator localOgelValidator = new LocalOgelValidator();
    List<LocalOgel> faultyLocalOgels = value.stream().filter(o -> !localOgelValidator.isValid(o, context)).collect(Collectors.toList());
    if (!faultyLocalOgels.isEmpty()) {
      getCustomizedErrorMessage(context, "A faulty local ogel data found : " + faultyLocalOgels.get(0).getId());
      return false;
    }
    List<LocalOgel> duplicateIdOgels = value.stream().filter(lo ->
        value.stream().anyMatch(lo2 -> lo.getId().equalsIgnoreCase(lo2.getId()) && !lo.equals(lo2))
    ).collect(Collectors.toList());
    if (!duplicateIdOgels.isEmpty()) {
      context.disableDefaultConstraintViolation();
      getCustomizedErrorMessage(context, "A duplicate ID found in bulk update data: " + duplicateIdOgels.get(0).getId());
      return false;
    }
    return true;
  }

  private void getCustomizedErrorMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message)
        .addConstraintViolation();
  }
}
