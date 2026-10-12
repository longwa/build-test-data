package beanvalidation

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

/**
 * Constrained with Jakarta Bean Validation annotations only, there is no constraints block. GORM validates the
 * annotations with Hibernate Validator, and Build Test Data translates them to generate data that passes.
 */
class Member {
    @NotBlank @Size(max = 30) String name
    @NotNull @Email String email
    @NotNull @Min(1L) @Max(5L) Integer level
    @NotNull @Positive BigDecimal balance
    @NotNull @Past Date joined
    @Pattern(regexp = /^[A-Z]{2}-\d{4}$/) String referenceCode
    @NotEmpty Set<Subscription> subscriptions

    static hasMany = [subscriptions: Subscription]
}
