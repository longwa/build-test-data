package base

import bookstore.Address
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import grails.buildtestdata.mixin.Build
import org.junit.Test

@Build(Address)
class MinSizeUnitTests {

    @Test
    void testEmailMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assertNotNull domainObject
        assertNotNull domainObject.id
        assertNotNull domainObject.emailAddress
        assertEquals domainObject.emailAddress.size(), 100
    }

    @Test
    void testUrlMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assertNotNull domainObject
        assertNotNull domainObject.id
        assertNotNull domainObject.webSite
        assertEquals domainObject.webSite.size(), 100
    }
}
