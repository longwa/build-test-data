import basetests.TestDateDomain
import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Rollback
@Integration
class DomainTestDataServiceDateTests extends Specification implements TestDataBuilder {
    void testDateNonNull() {
        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.testDate != null
    }

    void testDateMin() {
        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.testMinDate != null
        domainObject.testMinDate > new Date() + 99
    }

    void testDateMax() {
        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.testMaxDate != null
        domainObject.testMaxDate < new Date() - 99
    }

    void testDateInList() {
        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.testInListDate != null
        domainObject.testInListDate >= new Date() + 99
        domainObject.testInListDate <= new Date() + 101
    }

    void testDateRange() {
        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.testRangeDate != null
        domainObject.testRangeDate >= new Date() + 99
        domainObject.testRangeDate <= new Date() + 101
    }

    void testNonStandardValueSpecified() {
        def testValue = 1
        TestDataConfigurationHolder.sampleData = ['basetests.MonetaryValue': [
                currency: Currency.getInstance('USD'),
                amount: {-> (++testValue) }]
        ]

        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.monetaryValue != null
        2.0 == domainObject.monetaryValue.amount
        domainObject.monetaryValue.currency != null
    }

    void testTimestamp() {
        when:
        def domainObject = build(TestDateDomain)

        then:
        domainObject != null
        domainObject.testTimestamp != null
        domainObject.testTimestamp instanceof java.sql.Timestamp
    }
}
