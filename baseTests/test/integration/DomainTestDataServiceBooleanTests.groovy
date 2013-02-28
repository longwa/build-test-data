
class DomainTestDataServiceBooleanTests extends DomainTestDataServiceBase {

    void testBooleanDefaultGroovyTruthFalseOk() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Boolean testProperty
            }
        """)

        def domainObject = domainClass.build()
        assertNotNull domainObject
        assert domainObject.testProperty == false
    }

    void testBooleanManuallySetValues() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Boolean testProperty
            }
        """)

        def domainObject = domainClass.build(testProperty: true)
        assertNotNull domainObject
        assert domainObject.testProperty == true

        domainObject = domainClass.build(testProperty: false)
        assertNotNull domainObject
        assert domainObject.testProperty == false

    }
    
    void testBooleanNullable() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Boolean testProperty
                static constraints = {
                    testProperty(nullable: true)
                }
            }
        """)

        def domainObject = domainClass.build()
		
        assertNotNull domainObject
        assertNull domainObject.testProperty
    }

    void testBooleanNotNullable() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Boolean testProperty
            }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
    }
}
