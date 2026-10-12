package basetests

import grails.buildtestdata.TestData
import grails.validation.Validateable
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Jakarta Bean Validation annotations are translated to GORM constraints so the generated data satisfies them, see
 * grails.buildtestdata.validation.JakartaConstraintsEvaluator. GORM only enforces them with Hibernate (see
 * JakartaValidationHibSpec), the unit datastore ignores them, so this spec restores the Grails 8 nullable default to
 * show that the annotations alone decide what is populated.
 */
class JakartaValidationSpec extends Specification implements DomainTestBase {

    @Override
    Closure doWithConfig() {{ config ->
        config.grails.gorm.default.nullable = true
    }}

    void "the standard constraint annotations are satisfied"() {
        given:
        Class domainClass = createDomainClass("""
            import jakarta.validation.constraints.*
            import java.time.*

            @grails.persistence.Entity
            class JakartaStandard {
                Long id
                Long version
                @NotNull String notNull
                @NotBlank String notBlank
                @NotEmpty String notEmpty
                @NotNull @Size(min = 5, max = 8) String sized
                @Size(min = 5) String optionalSized
                @NotNull @Min(10L) Integer min
                @NotNull @Max(-5L) Long max
                @NotNull @DecimalMin(value = "1.5", inclusive = false) BigDecimal decimalMin
                @NotNull @DecimalMax("2.5") Double decimalMax
                @NotNull @Positive Integer positive
                @NotNull @PositiveOrZero Short positiveOrZero
                @NotNull @Negative Integer negative
                @NotNull @NegativeOrZero Long negativeOrZero
                @NotNull @Min(50L) @Digits(integer = 2, fraction = 0) Integer digits
                @NotNull @Email String email
                @NotNull @Pattern(regexp = /^[A-Z]{3}-\\d{2}\$/) String code
                @NotNull @AssertTrue Boolean agreed
                @NotNull @AssertFalse Boolean declined
                @NotNull @Past Date past
                @NotNull @PastOrPresent LocalDateTime pastOrPresent
                @NotNull @Future LocalDate future
                @NotNull @FutureOrPresent Instant futureOrPresent
                @Null String mustBeNull
                String unconstrained
            }
        """)

        when:
        def domainObject = domainClass.build()

        then:
        domainObject.id != null
        domainObject.notNull != null
        domainObject.notBlank?.trim()
        domainObject.notEmpty?.size() >= 1
        domainObject.sized.size() in 5..8
        domainObject.optionalSized == null
        domainObject.min >= 10
        domainObject.max <= -5
        domainObject.decimalMin > 1.5
        domainObject.decimalMax <= 2.5
        domainObject.positive > 0
        domainObject.positiveOrZero >= 0
        domainObject.negative < 0
        domainObject.negativeOrZero <= 0
        domainObject.digits in 50..99
        domainObject.email.contains('@')
        domainObject.code ==~ /[A-Z]{3}-\d{2}/
        domainObject.agreed == true
        domainObject.declined == false
        domainObject.past < new Date()
        domainObject.pastOrPresent <= LocalDateTime.now()
        domainObject.future > LocalDate.now()
        domainObject.futureOrPresent >= Instant.now()
        domainObject.mustBeNull == null
        domainObject.unconstrained == null

        when: "the optional properties are included"
        domainObject = domainClass.build(includes: '*')

        then:
        domainObject.optionalSized.size() >= 5
        domainObject.unconstrained != null

        and: "a @Null property is left alone even then"
        domainObject.mustBeNull == null
    }

    void "the annotations tighten the constraints block instead of replacing it"() {
        given:
        Class domainClass = createDomainClass("""
            import jakarta.validation.constraints.*

            @grails.persistence.Entity
            class JakartaTightened {
                Long id
                Long version
                @Size(min = 3) String name
                @Min(5L) Integer lowerBound
                @Max(3L) Integer upperBound
                @Digits(integer = 5, fraction = 0) Integer digits
                @Size(max = 20) String code
                @Pattern(regexp = /[a-z]+/) String matched

                static constraints = {
                    name nullable: false, maxSize: 4
                    lowerBound nullable: false, max: 10
                    upperBound nullable: false, min: 2
                    digits nullable: false, max: 100
                    code nullable: false, size: 2..5
                    matched nullable: false, matches: /[0-9]{3}/
                }
            }
        """)

        when:
        def domainObject = domainClass.build()

        then:
        domainObject.name.size() in 3..4
        domainObject.lowerBound in 5..10
        domainObject.upperBound in 2..3
        domainObject.digits <= 100
        domainObject.code.size() in 2..5

        and: "an existing matches constraint wins over @Pattern, there is no way to combine the two"
        domainObject.matched ==~ /[0-9]{3}/
    }

    void "annotations on the getter and on a superclass are honoured"() {
        given:
        GroovyClassLoader loader = new GroovyClassLoader()
        loader.parseClass("""
            import jakarta.validation.constraints.*

            abstract class JakartaBase {
                @NotNull @Size(min = 6) String inherited
            }

            @grails.persistence.Entity
            class JakartaGetter extends JakartaBase {
                Long id
                Long version
                String viaGetter

                @NotNull @Size(min = 4, max = 4)
                String getViaGetter() { viaGetter }
            }
        """)
        Class domainClass = loader.loadClass('JakartaGetter')
        mockDomain(domainClass)

        when:
        def domainObject = domainClass.build()

        then:
        domainObject.inherited.size() >= 6
        domainObject.viaGetter.size() == 4
    }

    void "a composed custom constraint is satisfied through the constraints composing it"() {
        given:
        Class domainClass = createDomainClass("""
            import basetests.jakarta.StrongCode

            @grails.persistence.Entity
            class JakartaComposed {
                Long id
                Long version
                @StrongCode String code
            }
        """)

        when:
        def domainObject = domainClass.build()

        then:
        domainObject.code.trim()
        domainObject.code.size() in 8..12
    }

    void "a custom constraint with its own validator is ignored, sample data can satisfy it"() {
        given:
        Class domainClass = createDomainClass("""
            import basetests.jakarta.Lucky
            import jakarta.validation.constraints.NotNull

            @grails.persistence.Entity
            class JakartaCustom {
                Long id
                Long version
                @NotNull @Lucky Integer number
            }
        """)

        expect: "the unit datastore does not validate the annotation, the plugin can only populate the property"
        domainClass.build().number == 0

        and:
        domainClass.build(number: 7).number == 7
    }

    void "a hasMany with a minimum size is filled with associated instances"() {
        given:
        GroovyClassLoader loader = new GroovyClassLoader()
        Class ownerClass = loader.parseClass("""
            import jakarta.validation.constraints.*

            @grails.persistence.Entity
            class JakartaOwner {
                Long id
                Long version
                @NotEmpty Set<JakartaItem> items
                @NotNull @Size(min = 2) Set<JakartaItem> moreItems
                static hasMany = [items: JakartaItem, moreItems: JakartaItem]
            }

            @grails.persistence.Entity
            class JakartaItem {
                Long id
                Long version
                String name
            }
        """)
        Class itemClass = loader.loadClass('JakartaItem')
        mockDomains(ownerClass, itemClass)

        when:
        def owner = ownerClass.build()

        then:
        owner.items.size() >= 1
        owner.moreItems.size() >= 2
    }

    void "the validation of the mocked domain itself is left alone"() {
        given:
        Class domainClass = createDomainClass("""
            import jakarta.validation.constraints.*

            @grails.persistence.Entity
            class JakartaUntouched {
                Long id
                Long version
                @NotNull @Size(min = 5) String name
            }
        """)

        when:
        def domainObject = domainClass.build()

        then:
        domainObject.name.size() >= 5

        and: "the GORM constraints of the domain class still only know what the constraints block declared"
        domainClass.constrainedProperties.name.nullable
        domainClass.constrainedProperties.name.minSize == null
        domainClass.newInstance().validate()
    }

    void "the annotations on a Validateable are honoured as well"() {
        when:
        JakartaCommand command = TestData.build(JakartaCommand)

        then:
        command.name.size() in 5..6
        command.quantity >= 3
    }
}

class JakartaCommand implements Validateable {
    @NotNull @Size(min = 5) String name
    @NotNull @jakarta.validation.constraints.Min(3L) Integer quantity

    static constraints = {
        name maxSize: 6
    }
}
