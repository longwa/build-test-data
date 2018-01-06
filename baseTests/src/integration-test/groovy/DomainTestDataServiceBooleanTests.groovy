import basetests.TestDomain
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class DomainTestDataServiceBooleanTests extends Specification implements TestDataBuilder {
    void testBooleanDefaultGroovyTruthFalseOk() {
        when:
        def domainObject = build(TestDomain)
        then:
        domainObject != null
        domainObject.testBoolean == false
    }

    void testBooleanManuallySetValues() {
        when:
        def domainObject = build(TestDomain, [testBoolean: true])
        then:
        domainObject != null
        domainObject.testBoolean == true

        when:
        domainObject = build(TestDomain, [testBoolean: false])
        then:
        domainObject != null
        domainObject.testBoolean == false
    }

    void testBooleanNullable() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testBooleanNull == null
    }

    void testBooleanNotNullable() {
        when:
        def domainObject = build(TestDomain)

        then:
        domainObject != null
        domainObject.testBoolean != null
    }
}

