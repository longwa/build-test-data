import grails.buildtestdata.TestDataConfigurationHolder

class DomainTestDataServiceDefaultTests extends DomainTestDataServiceBase {
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
        TestDataConfigurationHolder.reset()
    }

    void testPropertyNullable() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(nullable: true)
                }
            }
        """)

        def domainObject = domainClass.build()
        
        assertNotNull domainObject
        assertNull domainObject.testProperty
    }

    void testPropertyNotNullable() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty
            }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
    }

    void testFindRequiredPropertyNames() {
        def domainInstanceBuilder = createDomainInstanceBuilder("""
            class TestDomain {
                Long id
                Long version
                String nullableFalseProperty
                String nullableTrueProperty
                static constraints = {
                    nullableTrueProperty(nullable: true)
                }
            }
        """)

        assertEquals( ['nullableFalseProperty'], domainInstanceBuilder.requiredPropertyNames.asList() )

    }

    void testPropertySuppliedUnchanged() {
        def testProperty = 'myTestProperty'
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty
            }
        """)

        def domainObject = domainClass.build(testProperty: testProperty)

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty == testProperty
    }

    void testDefaultPropertyUnchanged() {
        def testProperty = 'myTestProperty'
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty == testProperty
    }

    void testDefaultPropertyOverridden() {
        def testProperty = 'myTestProperty'
        def overrideTestProperty = 'overrideTestProperty'
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        def domainObject = domainClass.build(testProperty: overrideTestProperty)

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty == overrideTestProperty
    }

    void testDefaultPropertyOverriddenByConfig() {
        def testProperty = 'myTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestDomain: [testProperty: defaultTestProperty]]
        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty == defaultTestProperty
    }

    void testDefaultPropertyOverriddenByConfigAndBuildParam() {
        def testProperty = 'myTestProperty'
        def overrideTestProperty = 'overrideTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty = '$testProperty'
            }
        """)

        TestDataConfigurationHolder.sampleData = [TestDomain: [testProperty: defaultTestProperty]]
        def domainObject = domainClass.build(testProperty: overrideTestProperty)

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty == overrideTestProperty
    }

    void testOddProperties() {
        def domainClass = createDomainClass("""
            class TestDomain {
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

        assertNotNull domainObject
        assertNotNull domainObject.currency
        assertNotNull domainObject.calendar
        assertNotNull domainObject.locale
        assertNotNull domainObject.timeZone
        assertNotNull domainObject.javaSqlDate
        assertNotNull domainObject.javaSqlTime
    }

    void testMatchConstraintApplyMatchingValidOk() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(matches:"[A-Z]+")
                }
            }
        """)
        
        TestDataConfigurationHolder.sampleData = [TestDomain: [testProperty: "ABC"]]
        
        def domainObject = domainClass.build()
        assertNotNull domainObject
        assertNotNull domainObject.testProperty == 'ABC'
    }

    void testValidatorConstraintApplyValidatorValidOk() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(validator:{ return ['ABC', 'CDE'].contains(it) })
                }
            }
        """)
        
        TestDataConfigurationHolder.sampleData = [TestDomain: [testProperty: "ABC"]]
        
        def domainObject = domainClass.build()
        assertNotNull domainObject
        assertNotNull domainObject.testProperty == 'ABC'
    }

    void testValidatorConstraintNotSupportedThrowsException() {
        def domainObject = [testProperty: 'testProperty']
        shouldFail {
            buildTestDataService.validatorHandler (
                domainObject, 'testProperty', [ processValidate: { target, propertyValues, errors -> true } ]
            )
        }

    }

    void testUniqueFalseConstraintOk() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty
                static constraints = {
                    testProperty(unique:false)
                }
            }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty

    }

    void testUniqueConstraintTrueNotSupportedThrowsException() {
        def domainObject = [testProperty: 'testProperty']
        shouldFail {buildTestDataService.uniqueHandler (domainObject, 'testProperty', [unique:true])}
    }
}
