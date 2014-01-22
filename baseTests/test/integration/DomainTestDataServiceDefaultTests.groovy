import grails.buildtestdata.TestDataConfigurationHolder
import org.junit.Test

class DomainTestDataServiceDefaultTests extends DomainTestDataServiceBase {
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
        TestDataConfigurationHolder.reset()
    }

    @Test
    void testPropertyNullable() {
        def domainClass = createDomainClass("""
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
        def domainClass = createDomainClass("""
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

    @Test
    void testFindRequiredPropertyNames() {
        def domainInstanceBuilder = createDomainInstanceBuilder("""
            class TestRequiredDomain {
                Long id
                Long version
                String nullableFalseProperty
                String nullableTrueProperty
                static constraints = {
                    nullableTrueProperty(nullable: true)
                }
            }
        """)

        assert ['nullableFalseProperty'] == domainInstanceBuilder.requiredPropertyNames.asList()

    }

    @Test
    void testPropertySuppliedUnchanged() {
        def testProperty = 'myTestProperty'
        def domainClass = createDomainClass("""
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

    @Test
    void testDefaultPropertyUnchanged() {
        def testProperty = 'myTestProperty'
        def domainClass = createDomainClass("""
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

    @Test
    void testDefaultPropertyOverridden() {
        def testProperty = 'myTestProperty'
        def overrideTestProperty = 'overrideTestProperty'
        def domainClass = createDomainClass("""
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

    @Test
    void testDefaultPropertyOverriddenByConfig() {
        def testProperty = 'myTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        def domainClass = createDomainClass("""
            class TestOverriddenConfigDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestOverriddenConfigDomain: [testProperty: defaultTestProperty]]
        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == defaultTestProperty
    }

    @Test
    void testDefaultPropertyOverriddenByConfigAndBuildParam() {
        def testProperty = 'myTestProperty'
        def overrideTestProperty = 'overrideTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        def domainClass = createDomainClass("""
            class TestConfigAndBuildParamDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestConfigAndBuildParamDomain: [testProperty: defaultTestProperty]]
        def domainObject = domainClass.build(testProperty: overrideTestProperty)

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == overrideTestProperty
    }

    @Test
    void testOddProperties() {
        def domainClass = createDomainClass("""
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

        assert domainObject != null
        assert domainObject.currency != null
        assert domainObject.calendar != null
        assert domainObject.locale != null
        assert domainObject.timeZone != null
        assert domainObject.javaSqlDate != null
        assert domainObject.javaSqlTime != null
    }

    @Test
    void testMatchConstraintApplyMatchingValidOk() {
        def domainClass = createDomainClass("""
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
        assert domainObject != null
        assert domainObject.testProperty == 'ABC'
    }

    @Test
    void testValidatorConstraintApplyValidatorValidOk() {
        def domainClass = createDomainClass("""
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
        assert domainObject != null
        assert domainObject.testProperty == 'ABC'
    }

    @Test
    void testValidatorConstraintNotSupportedThrowsException() {
        def domainObject = [testProperty: 'testProperty']
        shouldFail {
            buildTestDataService.validatorHandler (
                domainObject, 'testProperty', [ processValidate: { target, propertyValues, errors -> true } ]
            )
        }

    }

    @Test
    void testUniqueFalseConstraintOk() {
        def domainClass = createDomainClass("""
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

        assert domainObject != null
        assert domainObject.testProperty != null

    }

    @Test
    void testUniqueConstraintTrueNotSupportedThrowsException() {
        def domainObject = [testProperty: 'testProperty']
        shouldFail {buildTestDataService.uniqueHandler (domainObject, 'testProperty', [unique:true])}
    }
}
