import grails.buildtestdata.TestDataConfigurationHolder

class DomainTestDataServiceDateTests extends DomainTestDataServiceBase {
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDateMin() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(min: new Date() + 100)
                }
           }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty > new Date() + 99
    }

    void testDateMax() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(max: new Date() - 100)
                 }
           }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty < new Date() - 99
    }

    void testDateInList() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(inList: [new Date() + 100, new Date() + 200])
                }
           }
        """)

        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty >= new Date() + 99
        assertTrue domainObject.testProperty <= new Date() + 101
    }

    void testDateRange() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(range: new Date() + 100..new Date() + 200)
                }
           }
        """)
        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertTrue domainObject.testProperty >= new Date() + 99
        assertTrue domainObject.testProperty <= new Date() + 101
    }

    void testNonStandardValueSpecified() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version

                MonetaryValue monetaryValue

                static embedded = ['monetaryValue']
            }

            class MonetaryValue {
                Currency currency
                BigDecimal amount
            }
        """)

        def testValue = 1
        TestDataConfigurationHolder.sampleData = [MonetaryValue: [
                currency: Currency.getInstance('USD'),
                amount: {-> (++testValue) }]
        ]
        def domainObject = domainClass.build()

        assertNotNull domainObject
        assertNotNull domainObject.monetaryValue
        println domainObject.monetaryValue.amount
        assertTrue 2.0 == domainObject.monetaryValue.amount
        assertNotNull domainObject.monetaryValue.currency
    } 

}
