package test.optional.jars

import grails.test.*

class JodafiedDomainTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testJodafiedDomain() {
        def jodafiedDomain = JodafiedDomain.build()

        assertNotNull jodafiedDomain
        assertNotNull jodafiedDomain.id // saved OK
    }
}
