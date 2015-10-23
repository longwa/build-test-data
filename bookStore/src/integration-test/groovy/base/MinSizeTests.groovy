package base

import bookstore.Address
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback
import org.grails.core.DefaultGrailsDomainClass

@Rollback
@TestMixin(IntegrationTestMixin)
class MinSizeTests {
    void testEmailMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.emailAddress
        assert domainObject.emailAddress.size() == 100
    }

    void testUrlMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.webSite
        assert domainObject.webSite.size() == 100
    }
}
