package base

import grails.test.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import bookstore.Address

class MinSizeTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp()
     }

    protected void tearDown() {
        super.tearDown()
    }

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
