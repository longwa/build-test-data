import basetests.TestDomain
import grails.buildtestdata.TestDataBuilder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
@Rollback
class DomainTestDataServiceStringTests extends Specification implements TestDataBuilder {
    void testStringMinSize() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testMinProperty != null
        domainObject.testMinProperty.size() >= 200
    }

    void testStringMaxSizeNotExceeded() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testMaxProperty != null
        domainObject.testMaxProperty.size() <= 2
    }

    void testStringInList() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testInListProperty != null
        domainObject.testInListProperty == "one"
    }

    void testStringBlankFalse() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testBlankProperty != null
    }

    void testStringCreditCardNumber() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testCCProperty != null
    }

    void testStringEmail() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testEmailProperty != null
    }

    void testStringUrl() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testURLProperty != null
    }

    void testStringRange() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testRangeProperty != null
        domainObject.testRangeProperty == 'x'
    }

    void testStringSizeShrink() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testProperty != null
        domainObject.testStringSizeProperty.size() == 3
    }
}
