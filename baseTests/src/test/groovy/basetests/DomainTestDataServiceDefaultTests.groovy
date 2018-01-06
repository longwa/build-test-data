package basetests

import grails.buildtestdata.TestDataConfigurationHolder
import org.junit.Test
import spock.lang.Specification

class DomainTestDataServiceDefaultTests extends Specification implements DomainTestDataServiceBase  {

    void tearDown() {
        TestDataConfigurationHolder.reset()
    }


    void testPropertyNullable() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNullableDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(nullable: true)
                }
            }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty == null
    }

    void testPropertyNotNullable() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNotNullableDomain {
                Long id
                Long version
                String testProperty
            }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty != null
    }

//    void testFindRequiredPropertyNames() {
//        expect:
//        def domainInstanceBuilder = createDomainInstanceBuilder("""
//            @grails.persistence.Entity
//            class TestRequiredDomain {
//                Long id
//                Long version
//                String nullableFalseProperty
//                String nullableTrueProperty
//                static constraints = {
//                    nullableTrueProperty(nullable: true)
//                }
//            }
//        """)
//
//        assert ['nullableFalseProperty'] == domainInstanceBuilder.requiredPropertyNames.asList()
//
//    }


    void testPropertySuppliedUnchanged() {
        expect:
        def testProperty = 'myTestProperty'
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestSuppliedDomain {
                Long id
                Long version
                String testProperty
            }
        """)

        def domainObject = domainClass.build(testProperty: testProperty)

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == testProperty
    }

    void testDefaultPropertyUnchanged() {
        expect:
        def testProperty = 'myTestProperty'
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestUnchangedDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == testProperty
    }

    void testDefaultPropertyOverridden() {
        expect:
        def testProperty = 'myTestProperty'
        def overrideTestProperty = 'overrideTestProperty'
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestOverriddenDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        def domainObject = domainClass.build(testProperty: overrideTestProperty)

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == overrideTestProperty
    }


    void testDefaultPropertyOverriddenByConfig() {
        when:
        def testProperty = 'myTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestOverriddenConfigDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestOverriddenConfigDomain: [testProperty: defaultTestProperty]]
        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == defaultTestProperty
    }

    void testDefaultPropertyOverriddenByConfigAndBuildParam() {
        when:
        def testProperty = 'myTestProperty'
        def overrideTestProperty = 'overrideTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestConfigAndBuildParamDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestConfigAndBuildParamDomain: [testProperty: defaultTestProperty]]
        def domainObject = domainClass.build(testProperty: overrideTestProperty)

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == overrideTestProperty
    }

    void testOddProperties() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestOddDomain {
                Long id
                Long version
                Currency  currency
                Calendar calendar
                Locale locale
                TimeZone timeZone
                java.sql.Date javaSqlDate
                java.sql.Time javaSqlTime
            }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.currency != null
        assert domainObject.calendar != null
        assert domainObject.locale != null
        assert domainObject.timeZone != null
        assert domainObject.javaSqlDate != null
        assert domainObject.javaSqlTime != null
    }

    void testMatchConstraintApplyMatchingValidOk() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestMatchDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(matches:"[A-Z]+")
                }
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestMatchDomain: [testProperty: "ABC"]]

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty == 'ABC'
    }

    void testValidatorConstraintApplyValidatorValidOk() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestValidatorConstraintDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(validator:{ return ['ABC', 'CDE'].contains(it) })
                }
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestValidatorConstraintDomain: [testProperty: "ABC"]]

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty == 'ABC'
    }

    void testValidatorConstraintNotSupportedThrowsException() {
        expect:
        def domainObject = [testProperty: 'testProperty']
        try {
            buildTestDataService.validatorHandler (
                domainObject, 'testProperty', [ processValidate: { target, propertyValues, errors -> true } ]
            )
            fail()
        }
        catch(ignored) {
        }
    }

    void testUniqueFalseConstraintOk() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestUniqueFalseDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(unique:false)
                }
            }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null

    }

    void testUniqueConstraintTrueNotSupportedThrowsException() {
        expect:
        def domainObject = [testProperty: 'testProperty']
        try {
            buildTestDataService.uniqueHandler (domainObject, 'testProperty', [unique:true])
            fail()
        }
        catch(ignored) {
        }
    }
}
