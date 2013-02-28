package base

import bookstore.Address
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import grails.buildtestdata.mixin.Build

@Build(Address)
class MinSizeUnitTests {

    void testEmailMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assertNotNull domainObject
        assertNotNull domainObject.id
        assertNotNull domainObject.emailAddress
        assertEquals domainObject.emailAddress.size(), 100
    }

    void testUrlMinSize() {
        def addressDomain = new DefaultGrailsDomainClass(Address)
        def domainObject = Address.build()
        assertNotNull domainObject
        assertNotNull domainObject.id
        assertNotNull domainObject.webSite
        assertEquals domainObject.webSite.size(), 100
    }

 
}
