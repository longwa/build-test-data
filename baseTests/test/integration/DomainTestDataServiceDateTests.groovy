import grails.buildtestdata.TestDataConfigurationHolder
import org.junit.Test

class DomainTestDataServiceDateTests extends DomainTestDataServiceBase {

    @Test
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

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty > new Date() + 99
    }

    @Test
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

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty < new Date() - 99
    }

    @Test
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

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty >= new Date() + 99
        assert domainObject.testProperty <= new Date() + 101
    }

    @Test
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

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty >= new Date() + 99
        assert domainObject.testProperty <= new Date() + 101
    }

    @Test
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

        assert domainObject != null
        assert domainObject.monetaryValue != null
        assert 2.0 == domainObject.monetaryValue.amount
        assert domainObject.monetaryValue.currency != null
    }

    @Test
    void testTimestamp() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                java.sql.Timestamp testProperty
           }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty instanceof java.sql.Timestamp
    }

}
