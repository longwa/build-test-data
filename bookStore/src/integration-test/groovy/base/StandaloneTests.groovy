package base

import org.junit.Test
import standalone.Standalone

class StandaloneTests extends GroovyTestCase {

    @Test
    void testNullableBelongsToNotFollowed() {
        def standalone = Standalone.build() // standalone.Standalone has a "parent" property on it that is nullable (otherwise it'd get in an infinite loop)
        assertNotNull standalone
        assertNotNull standalone.id
        assertNull standalone.parent
        assert standalone.emailAddress != null
    }

    @Test
    void testBuildStandalonePassAllVariables() {
        def created = new Date()
        def obj = Standalone.build(name: "Foo", age: 14, created: created, emailAddress: "foo@bar.com")
        assertValidDomainObject(obj)
        assertEquals "Foo", obj.name
        assertEquals 14, obj.age
        assertEquals created, obj.created
        assert "foo@bar.com" == obj.emailAddress
    }

    @Test
    void testBuildStandalonePassNoVariables() {
        def obj = Standalone.build()
        assertValidDomainObject(obj)
        assertEquals "name", obj.name  // by default it just uses the property name for the value for strings
        assertNotNull obj.created
        assertEquals 0, obj.age
    }

    @Test
    void testUniqueEmailAddressWorksFirstThenFails() {
        def first = Standalone.build()
        assert first.emailAddress != null
        assert first.emailAddress == "a@b.com"

        // to do this right you'd need to override in the TestDataConfig.groovy file with a custom closure
        def second
        shouldFail {
            second = Standalone.build()
        }
        assert second == null
    }

    void assertValidDomainObject(domainObject) {
        assertNotNull domainObject
        assertNotNull domainObject.id
        assertEquals 0, domainObject.errors.errorCount
    }
}
