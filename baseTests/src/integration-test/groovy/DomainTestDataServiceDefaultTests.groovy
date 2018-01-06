import basetests.TestMatchDomain
import basetests.TestNullableDomain
import basetests.TestOddDomain
import basetests.TestUniqueDomain
import basetests.TestValidatorDomain
import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.handler.ConstraintHandlerException
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Rollback
@Integration
class DomainTestDataServiceDefaultTests extends Specification implements TestDataBuilder {
    void setup() {
        TestDataConfigurationHolder.reset()
    }

    void testPropertyNullable() {
        when:
        def domainObject = build(TestNullableDomain)

        then:
        domainObject != null
        domainObject.testNullableProperty == null
    }

    void testPropertyNotNullable() {
        when:
        def domainObject = build(TestNullableDomain)

        then:
        domainObject != null
        domainObject.testProperty != null
    }

    void testPropertySuppliedUnchanged() {
        def testProperty = 'myTestProperty'

        when:
        def domainObject = build(TestNullableDomain, [testProperty: testProperty])

        then:
        domainObject != null
        domainObject.testProperty != null
        domainObject.testProperty == testProperty
    }

    void testDefaultPropertyUnchanged() {
        when:
        def domainObject = build(TestNullableDomain)

        then:
        domainObject != null
        domainObject.testDefaultProperty != null
        domainObject.testDefaultProperty == 'default'
    }

    void testDefaultPropertyOverridden() {
        def overrideTestProperty = 'overrideTestProperty'

        when:
        def domainObject = build(TestNullableDomain, [testProperty: overrideTestProperty])

        then:
        domainObject != null
        domainObject.testProperty != null
        domainObject.testProperty == overrideTestProperty
    }

    void testDefaultPropertyOverriddenByConfig() {
        def defaultTestProperty = 'defaultTestProperty'
        TestDataConfigurationHolder.sampleData = ['basetests.TestNullableDomain': [testDefaultProperty: defaultTestProperty]]

        when:
        def domainObject = build(TestNullableDomain)

        then:
        assert domainObject != null
        assert domainObject.testDefaultProperty != null
        assert domainObject.testDefaultProperty == defaultTestProperty
    }

    void testDefaultPropertyOverriddenByConfigAndBuildParam() {
        def overrideTestProperty = 'overrideTestProperty'
        def defaultTestProperty = 'defaultTestProperty'
        TestDataConfigurationHolder.sampleData = [TestConfigAndBuildParamDomain: [testProperty: defaultTestProperty]]

        when:
        def domainObject = build(TestNullableDomain, [testProperty: overrideTestProperty])

        then:
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.testProperty == overrideTestProperty
    }

    void testOddProperties() {
        when:
        def domainObject = build(TestOddDomain)

        then:
        domainObject != null
        domainObject.currency != null
        domainObject.calendar != null
        domainObject.locale != null
        domainObject.timeZone != null
        domainObject.javaSqlDate != null
        domainObject.javaSqlTime != null
    }

    void testMatchConstraintApplyMatchingValidOk() {
        when:
        def domainObject = build(TestMatchDomain)

        then:
        domainObject != null
        domainObject.testProperty =~ /[A-Z]+/
    }

    void testValidatorConstraintApplyValidatorValidOk() {
        TestDataConfigurationHolder.sampleData = ['basetests.TestValidatorDomain': [testProperty: "ZZZ"]]

        when:
        def domainObject = build(TestValidatorDomain)

        then:
        domainObject != null
        domainObject.testProperty == 'ZZZ'
    }

    void testValidatorConstraintNotSupportedFails() {
        when:
        build(TestValidatorDomain)

        then:
        thrown(ConstraintHandlerException)
    }

    void testUniqueConstraintAllowedButNotImplemented() {
        when:
        def domainObject = build(TestUniqueDomain)

        then:
        domainObject != null
        domainObject.testProperty != null
    }
}
