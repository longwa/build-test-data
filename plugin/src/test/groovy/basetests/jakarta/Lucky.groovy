package basetests.jakarta

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * A custom constraint with its own validator, there is no way to derive a valid value from it
 */
@Constraint(validatedBy = [LuckyValidator])
@Documented
@Target([ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@interface Lucky {
    String message() default 'must be the lucky number'
    Class<?>[] groups() default []
    Class<? extends Payload>[] payload() default []
}

class LuckyValidator implements ConstraintValidator<Lucky, Integer> {
    static final int LUCKY_NUMBER = 7

    @Override
    boolean isValid(Integer value, ConstraintValidatorContext context) {
        value == null || value == LUCKY_NUMBER
    }
}
