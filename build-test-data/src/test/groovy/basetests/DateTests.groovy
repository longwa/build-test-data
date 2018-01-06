package basetests

import grails.buildtestdata.TestDataConfigurationHolder
import org.junit.Test
import spock.lang.Specification

class DateTests extends Specification implements DomainTestBase {

    void testDateMin() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDateMin {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(min: new Date() + 100)
                }
           }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty > new Date() + 99
    }

    void testDateMax() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDateMax {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(max: new Date() - 100)
                 }
           }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty < new Date() - 99
    }


    void testDateInList() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDateInList {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(inList: [new Date() + 100, new Date() + 200])
                }
           }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty >= new Date() + 99
        assert domainObject.testProperty <= new Date() + 101
    }

    void testDateRange() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDateRange {
                Long id
                Long version
                Date testProperty

                static constraints = {
                    testProperty(range: new Date() + 100..new Date() + 200)
                }
           }
        """)
        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty >= new Date() + 99
        assert domainObject.testProperty <= new Date() + 101
    }

    void testNonStandardValueSpecified() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNonStandardValueSpecified {
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

        then:
        assert domainObject != null
        assert domainObject.monetaryValue != null
        assert 2.0 == domainObject.monetaryValue.amount
        assert domainObject.monetaryValue.currency != null
    }

    void testTimestamp() {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestTimestamp {
                Long id
                Long version
                java.sql.Timestamp testProperty
           }
        """)

        def domainObject = domainClass.build()

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty instanceof java.sql.Timestamp
    }

}
