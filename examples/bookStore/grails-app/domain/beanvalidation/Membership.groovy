package beanvalidation

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

/**
 * A domain class constrained with Jakarta Bean Validation annotations instead of a constraints block. GORM validates
 * it with Hibernate Validator, Build Test Data translates the annotations so the generated data passes.
 */
class Membership {
    @NotBlank @Size(max = 20) String memberName
    @NotNull @Email String email
    @NotNull @Min(1L) @Max(5L) Integer level
    @NotNull @Positive BigDecimal fee
    @NotNull @Past Date joined
}
