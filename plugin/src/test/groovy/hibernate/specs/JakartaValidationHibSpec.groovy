package hibernate.specs

import basetests.jakarta.Lucky
import grails.gorm.validation.ConstrainedEntity
import grails.test.hibernate.HibernateSpec
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

import static grails.buildtestdata.TestData.build

/**
 * With Hibernate, GORM validates an entity carrying Bean Validation annotations with Hibernate Validator instead of
 * its own constraints, so the generated data has to satisfy the annotations for the save to succeed
 */
class JakartaValidationHibSpec extends HibernateSpec {

    List<Class> getDomainClasses() { [JakartaAccount, JakartaLucky] }

    void "GORM validates the entity with Bean Validation rather than its constraints"() {
        expect:
        !(JakartaAccount.gormPersistentEntity.mappingContext.getEntityValidator(JakartaAccount.gormPersistentEntity) instanceof ConstrainedEntity)
    }

    void "an entity with annotations is built, saved and valid"() {
        when:
        JakartaAccount account = build(JakartaAccount)

        then:
        account.id != null
        account.validate()
        account.name.trim()
        account.code.size() in 5..10
        account.email.contains('@')
        account.age >= 18
        account.reference ==~ /[A-Z]{2}\d{4}/
        account.opened < new Date()
        account.commented ==~ /(?ix)[a-z]{3} - \d # three letters, a dash and a digit/

        and: "the constraints block still drives the schema, so it is satisfied too"
        account.fromBlock != null
        JakartaAccount.count() == 1
    }

    void "a custom constraint with its own validator needs sample data"() {
        when:
        build(JakartaLucky)

        then:
        Exception e = thrown()
        e.class.simpleName == 'ValidationException'

        when:
        JakartaLucky lucky = build(JakartaLucky, [number: 7])

        then:
        lucky.id != null
        lucky.validate()
    }
}

@grails.persistence.Entity
class JakartaAccount {
    @NotBlank String name
    @Size(min = 5, max = 10) String code
    @NotNull @Email String email
    @NotNull @Min(18L) Integer age
    @NotNull @Pattern(regexp = /^[A-Z]{2}\d{4}$/) String reference
    @NotNull @Pattern(regexp = '[a-z]{3} - \\d # three letters, a dash and a digit', flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.COMMENTS]) String commented
    @NotNull @Past Date opened
    String fromBlock

    static constraints = {
        fromBlock nullable: false, blank: false
    }
}

@grails.persistence.Entity
class JakartaLucky {
    @NotNull @Lucky Integer number
}
