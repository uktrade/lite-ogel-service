package uk.gov.bis.lite.ogel.validator;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import uk.gov.bis.lite.ogel.model.localOgel.LocalOgel;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class LocalOgelListValidator implements ConstraintValidator<CheckLocalOgelList, List<LocalOgel>> {

  @Override
  public void initialize(CheckLocalOgelList constraintAnnotation) {
  }

  @Override
  public boolean isValid(List<LocalOgel> value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    OptionalInt ogelWithoutIdIndex = IntStream.range(0, value.size())
        .filter(i -> value.get(i).getId() == null).findAny();
    if (ogelWithoutIdIndex.isPresent()) {
      getCustomizedErrorMessage(context, "Local Ogel Without ID is not allowed! Index: " + ogelWithoutIdIndex.getAsInt());
      return false;
    }
    LocalOgelValidator localOgelValidator = new LocalOgelValidator();
    Optional<LocalOgel> invalidLocalOgelExists = value.stream().filter(o -> !localOgelValidator.isValid(o, context)).findAny();
    List<ConstraintViolationCreationContext> violationCreationContextList =
        ((ConstraintValidatorContextImpl) context).getConstraintViolationCreationContexts();
    if (invalidLocalOgelExists.isPresent()) {
      StringBuilder errorSB = new StringBuilder("A faulty local ogel data found : ");
      violationCreationContextList.forEach(err -> errorSB.append(err.getMessage()).append("\n"));
      getCustomizedErrorMessage(context, errorSB.toString());
      return false;
    }
    List<LocalOgel> duplicateIdOgels = value.stream().filter(lo ->
        value.stream().anyMatch(lo2 -> lo.getId().equalsIgnoreCase(lo2.getId()) && !lo.equals(lo2))
    ).collect(Collectors.toList());
    if (!duplicateIdOgels.isEmpty()) {
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
