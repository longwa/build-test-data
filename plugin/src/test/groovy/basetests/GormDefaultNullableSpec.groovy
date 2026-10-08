package basetests

import spock.lang.Specification

/**
 * Grails 8 makes unconstrained domain properties nullable. The builder only populates the properties GORM reports as
 * required, so with the Grails 8 default a property needs an explicit nullable: false to be populated.
 *
 * The rest of the test suite sets grails.gorm.default.nullable to false in application.yml, this spec restores the
 * Grails 8 default.
 */
class GormDefaultNullableSpec extends Specification implements DomainTestBase {

    @Override
    Closure doWithConfig() {{ config ->
        config.grails.gorm.default.nullable = true
    }}

    void "unconstrained properties are left null and nullable false properties are populated"() {
        given:
        Class domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestGormDefaultNullable {
                Long id
                Long version
                String unconstrained
                Integer unconstrainedNumber
                String required
                Integer requiredNumber

                static constraints = {
                    required nullable: false
                    requiredNumber nullable: false, min: 5
                }
            }
        """)

        when:
        def domainObject = domainClass.build()

        then:
        domainObject.id != null
        domainObject.unconstrained == null
        domainObject.unconstrainedNumber == null
        domainObject.required != null
        domainObject.requiredNumber >= 5
    }
}
