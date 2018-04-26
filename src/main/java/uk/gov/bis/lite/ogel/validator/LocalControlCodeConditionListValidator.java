package uk.gov.bis.lite.ogel.validator;

import uk.gov.bis.lite.ogel.model.local.ogel.LocalControlCodeCondition;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocalControlCodeConditionListValidator
    implements ConstraintValidator<CheckLocalControlCodeConditionList, List<LocalControlCodeCondition>> {

  @Override
  public void initialize(CheckLocalControlCodeConditionList constraintAnnotation) {
  }

  @Override
  public boolean isValid(List<LocalControlCodeCondition> value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    // Check for missing OGEL IDs
    OptionalInt withoutOgelIDIndex = IntStream.range(0, value.size())
        .filter(i -> value.get(i).getOgelID() == null).findAny();
    if (withoutOgelIDIndex.isPresent()) {
      getCustomizedErrorMessage(context, "OGEL Control Code Condition without OGEL ID is not allowed, Index: " + withoutOgelIDIndex.getAsInt());
      return false;
    }

    // Check for missing Control Codes
    OptionalInt withoutControlCodeIndex = IntStream.range(0, value.size())
        .filter(i -> value.get(i).getControlCode() == null).findAny();
    if (withoutControlCodeIndex.isPresent()) {
      getCustomizedErrorMessage(context, "OGEL Control Code Condition without Control Code is not allowed, Index: " + withoutControlCodeIndex.getAsInt());
      return false;
    }

    List<LocalControlCodeCondition> duplicateControlCodeConditions = value.stream().filter(lo ->
        value.stream().anyMatch(lo2 -> lo.getOgelID().equalsIgnoreCase(lo2.getOgelID()) && lo.getControlCode().equalsIgnoreCase(lo2.getControlCode()) && !lo.equals(lo2))
    ).collect(Collectors.toList());
    if (!duplicateControlCodeConditions.isEmpty()) {

      String duplicates = duplicateControlCodeConditions
          .stream()
          .map(e -> e.getOgelID() + "/" + e.getControlCode())
          .distinct()
          .collect(Collectors.joining(", "));

      getCustomizedErrorMessage(context, "Duplicate OGEL Control Code Conditions found in bulk update data: " + duplicates);
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
