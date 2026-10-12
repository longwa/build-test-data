package basetests.jakarta

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * A custom constraint composed of standard ones, Bean Validation validates it through the composing constraints
 */
@NotBlank
@Size(min = 8, max = 12)
@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Documented
@Target([ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@interface StrongCode {
    String message() default 'must be a strong code'
    Class<?>[] groups() default []
    Class<? extends Payload>[] payload() default []
}
