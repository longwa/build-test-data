package basetests

import spock.lang.Specification

class DomainTestDataServiceBooleanTests extends Specification implements DomainTestDataServiceBase {

    void testBooleanDefaultGroovyTruthFalseOk() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain {
                Long id
                Long version
                Boolean testProperty
            }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty == false
    }

    void testBooleanManuallySetValues() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain2 {
                Long id
                Long version
                Boolean testProperty
            }
        """)

        def domainObject = domainClass.build(testProperty: true)

        then:
        assert domainObject != null
        assert domainObject.testProperty == true

        when:
        domainObject = domainClass.build(testProperty: false)

        then:
        assert domainObject != null
        assert domainObject.testProperty == false

    }

    void testBooleanNullable() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain3 {
                Long id
                Long version
                Boolean testProperty
                static constraints = {
                    testProperty(nullable: true)
                }
            }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty == null
    }

    void testBooleanNotNullable() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain4 {
                Long id
                Long version
                Boolean testProperty
            }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty != null
    }
}
