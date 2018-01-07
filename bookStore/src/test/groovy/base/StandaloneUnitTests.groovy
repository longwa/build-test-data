package base

import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification
import standalone.Standalone

class StandaloneUnitTests extends Specification implements UnitTestDataBuilder {
    @Override
    Class[] getDomainClassesToMock() {
        [Standalone]
    }

    void testNullableBelongsToNotFollowed() {
        when:
        def standalone = build(Standalone) // standalone.Standalone has a "parent" property on it that is nullable (otherwise it'd get in an infinite loop)

        then:
        standalone
        standalone.id
        !standalone.parent
        standalone.emailAddress != null
    }

    void testBuildStandalonePassAllVariables() {
        def created = new Date()

        when:
        def obj = build(Standalone, [name: "Foo", age: 14, created: created, emailAddress: "foo@bar.com"])

        then:
        assertValidDomainObject(obj)
        obj.name == "Foo"
        obj.age == 14
        obj.created == created
        "foo@bar.com" == obj.emailAddress
    }

    void testBuildStandalonePassNoVariables() {
        when:
        def obj = build(Standalone)

        then:
        assertValidDomainObject(obj)

        obj.name  // by default it just uses the property name for the value for strings == "name"
        obj.created
        obj.age == 0
    }

    private void assertValidDomainObject(domainObject) {
        assert domainObject
        assert domainObject.id
        assert domainObject.errors.errorCount == 0
    }
}
