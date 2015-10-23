package base

import grails.test.*

class BuildAllTests extends GroovyTestCase {
    def grailsApplication

    protected void setUp() {
        super.setUp()
     }

    protected void tearDown() {
        super.tearDown()
    }

    void testBuildAllDomains() {
        def successful = true
		def caughtError
        grailsApplication.domainClasses.each { domainClass ->
            log.info "Test of ${domainClass.name}.build()"
            try {
                def domainObject = domainClass.clazz.build()
                assertNotNull domainObject."${domainClass.identifier.name}"
                log.info "********** SUCCESSFUL BUILD OF $domainClass"
            } catch (Exception e) {
                log.severe "********** FAILED BUILD OF $domainClass: $e"
				caughtError = e
                successful = false
            }
        }
		if (!successful) {
			throw new Exception("Caught exception while building all domain classes", caughtError)
		}
    }

}
