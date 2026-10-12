package beanvalidation

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class Subscription {
    @NotBlank String plan
    @NotNull Member member

    static belongsTo = [member: Member]
}
