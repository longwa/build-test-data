package grails.buildtestdata.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/** A custom constraint with its own validator, nothing can be derived from it */
@Constraint(validatedBy = [MysteryValidator])
@Target([ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@interface Mystery {
    String message() default 'mystery'
    Class<?>[] groups() default []
    Class<? extends Payload>[] payload() default []
}

class MysteryValidator implements ConstraintValidator<Mystery, String> {
    @Override
    boolean isValid(String value, ConstraintValidatorContext context) { true }
}
