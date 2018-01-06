package base

import bookstore.Address
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Test

@Rollback
@Integration
class MinSizeTests {

    @Test
    void testEmailMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.emailAddress
        assert domainObject.emailAddress.size() == 100
    }

    @Test
    void testUrlMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.webSite
        assert domainObject.webSite.size() == 100
    }
}
