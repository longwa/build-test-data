package grails.buildtestdata.validation

import grails.buildtestdata.handler.MatchesConstraintHandler
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.DefaultConstrainedProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultConstraintRegistry
import org.hibernate.validator.constraints.CreditCardNumber
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import org.hibernate.validator.constraints.URL
import org.springframework.context.support.StaticMessageSource
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime

class JakartaConstraintsEvaluatorSpec extends Specification {

    ConstraintRegistry registry = new DefaultConstraintRegistry(new StaticMessageSource())
    JakartaConstraintsEvaluator evaluator = new JakartaConstraintsEvaluator()

    void "the constraint annotations on fields and getters are found, repeated ones included"() {
        when:
        Map<String, List> annotations = JakartaConstraintsEvaluator.findConstraintAnnotations(Annotated)

        then:
        annotations.keySet() == ['name', 'code', 'website', 'ignored', 'gone'] as Set
        annotations.name*.annotationType() == [NotNull, Size]
        annotations.code*.annotationType() == [Pattern, Pattern]
        annotations.website*.annotationType() == [URL]
        JakartaConstraintsEvaluator.hasConstraintAnnotations(Annotated)
        !JakartaConstraintsEvaluator.hasConstraintAnnotations(Object)
    }

    void "the existing constraints are copied and tightened, not modified"() {
        given:
        DefaultConstrainedProperty name = new DefaultConstrainedProperty(Annotated, 'name', String, registry)
        name.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, true)
        name.applyConstraint(ConstrainedProperty.MAX_SIZE_CONSTRAINT, 4)
        name.addMetaConstraint('example', 'Bob')
        Map<String, ConstrainedProperty> original = [name: name]

        when:
        Map<String, ConstrainedProperty> merged = evaluator.apply(Annotated, original, registry)

        then:
        !merged.is(original)
        !merged.name.is(name)
        !merged.name.nullable
        merged.name.minSize == 3
        merged.name.maxSize == 4
        ((DefaultConstrainedProperty) merged.name).getMetaConstraintValue('example') == 'Bob'

        and:
        name.nullable
        name.minSize == null
        original.size() == 1

        and: "unsupported annotations are reported"
        evaluator.unsupportedAnnotations == [ignored: [Mystery]]
    }

    void "a class without annotations gets its constraints back untouched"() {
        given:
        Map<String, ConstrainedProperty> original = [:]

        expect:
        evaluator.apply(Object, original, registry).is(original)
    }

    void "a property annotated with @Null is dropped"() {
        given:
        DefaultConstrainedProperty gone = new DefaultConstrainedProperty(Annotated, 'gone', String, registry)
        gone.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, false)

        expect:
        !evaluator.apply(Annotated, [gone: gone], registry).containsKey('gone')
    }

    void "the Hibernate Validator annotations with a GORM equivalent are translated by name"() {
        when:
        Map<String, ConstrainedProperty> merged = evaluator.apply(HibernateAnnotated, [:], registry)

        then:
        merged.name.minSize == 2
        merged.name.maxSize == 3
        merged.level.min == 1
        merged.level.max == 5
        merged.website.url
        merged.card.creditCard

        and: "nothing made them required"
        merged.values().every { it.nullable }
    }

    @Unroll
    void "a bound of #value is converted to #expected for a #type.simpleName property"() {
        expect:
        JakartaConstraintsEvaluator.convertNumber(value, type, rounding) == expected

        where:
        value                 | type       | rounding             || expected
        1.5                   | Integer    | RoundingMode.CEILING || 2
        1.5                   | Integer    | RoundingMode.FLOOR   || 1
        10L                   | int        | RoundingMode.CEILING || 10
        10L                   | Long       | RoundingMode.CEILING || 10L
        10L                   | Short      | RoundingMode.CEILING || (short) 10
        10L                   | Byte       | RoundingMode.CEILING || (byte) 10
        10L                   | BigInteger | RoundingMode.CEILING || 10G
        1.5                   | BigDecimal | RoundingMode.CEILING || 1.5G
        1.5                   | Double     | RoundingMode.CEILING || 1.5d
        1.5                   | Float      | RoundingMode.CEILING || 1.5f
        10_000_000_000L       | Integer    | RoundingMode.CEILING || null
        300L                  | Byte       | RoundingMode.CEILING || null
        1                     | String     | RoundingMode.CEILING || null
    }

    @Unroll
    void "a #type.simpleName one unit away from now is #description"() {
        when:
        Comparable value = JakartaConstraintsEvaluator.temporalFromNow(type, offset)

        then:
        type.isInstance(value)
        offset < 0 ? value < type.invokeMethod('now', null) : value > type.invokeMethod('now', null)

        where:
        type           | offset
        Instant        | -1
        LocalDate      | 1
        LocalDateTime  | -1
        ZonedDateTime  | 1
        OffsetDateTime | -1
        Year           | 1
        YearMonth      | -1
        description = offset < 0 ? 'in the past' : 'in the future'
    }

    void "legacy date types are supported and time of day types are not"() {
        expect:
        JakartaConstraintsEvaluator.temporalFromNow(Date, -1) < new Date()
        JakartaConstraintsEvaluator.temporalFromNow(Timestamp, 1) > new Timestamp(System.currentTimeMillis())
        JakartaConstraintsEvaluator.temporalFromNow(java.sql.Date, -1) < new Date()
        JakartaConstraintsEvaluator.temporalFromNow(LocalTime, -1) == null
    }

    @Unroll
    void "the pattern #regex is rewritten to #expected for the generator"() {
        expect:
        MatchesConstraintHandler.toAutomatonRegex(regex) == expected

        where:
        regex               || expected
        /^[A-Z]{3}$/        || /[A-Z]{3}/
        /[A-Z]{3}/          || /[A-Z]{3}/
        /^[A-Z]{3}/         || /[A-Z]{3}/
        /[A-Z]{3}\$/        || /[A-Z]{3}\$/
        /^\d{3}-\w+$/       || /[0-9]{3}-[a-zA-Z0-9_]+/
        /[\d_]+/            || /[0-9_]+/
        /[\D]+/             || /[\D]+/
        /\D\W/              || /[^0-9][^a-zA-Z0-9_]/
        /a\\d/              || /a\\d/
        /\./                || /\./
        'a\\sb'             || 'a[ \t\n\r\f]b'
    }

    void "the flags of a @Pattern become an embedded flag expression of the matches constraint"() {
        when:
        Map<String, ConstrainedProperty> merged = evaluator.apply(Flagged, [:], registry)

        then:
        merged.code.matches == '(?ix)[a-z]+ # letters only'
        merged.plain.matches == '[a-z]+'
        merged.canonical.matches == '[a-z]+'
    }

    @Unroll
    void "the flag expression of #regex is handled for the generator: #expected"() {
        expect:
        MatchesConstraintHandler.toAutomatonRegex(regex) == expected

        where:
        regex                                  || expected
        '(?i)[a-z]+'                           || '[a-z]+'
        '(?ims)^[a-z]+$'                       || '[a-z]+'
        '(?x)[a-z]+ # letters only'            || '[a-z]+'
        '(?x)[a-z ]{2} \\ [0-9] # a space'      || '[a-z ]{2}\\ [0-9]'
        '(?x)a b\n# comment\nc'               || 'abc'
        '(?i)'                                 || ''
    }

    @Unroll
    void "a value matching #regex is generated"() {
        expect:
        new nl.flotsam.xeger.Xeger(MatchesConstraintHandler.toAutomatonRegex(regex)).generate() ==~ regex

        where:
        regex << [/^[A-Z]{2}-\d{4}$/, /\w{3}\s\d/, /[\d_]{2}\.[a-z]+/, /\D{2}/, '(?i)^[a-z]{3}$', '(?x)[a-z]+ - \\d # dash digit']
    }

    @SuppressWarnings('unused')
    static class Annotated {
        @NotNull @Size(min = 3) String name
        @Pattern(regexp = /[a-z]+/) @Pattern(regexp = /.{3}/) String code
        @Mystery String ignored
        @Null String gone
        String website

        @URL
        String getWebsite() { website }
    }

    @SuppressWarnings('unused')
    static class Flagged {
        @Pattern(regexp = '[a-z]+ # letters only', flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.COMMENTS]) String code
        @Pattern(regexp = '[a-z]+') String plain
        @Pattern(regexp = '[a-z]+', flags = Pattern.Flag.CANON_EQ) String canonical
    }

    @SuppressWarnings('unused')
    static class HibernateAnnotated {
        @Length(min = 2, max = 3) String name
        @Range(min = 1L, max = 5L) Integer level
        @URL String website
        @CreditCardNumber String card
    }
}
