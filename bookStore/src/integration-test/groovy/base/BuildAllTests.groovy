package base

import grails.core.GrailsDomainClass
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback

@Rollback
@TestMixin(IntegrationTestMixin)
class BuildAllTests {
    def grailsApplication

    void testBuildAllDomains() {
        boolean successful = true
        Exception caughtError = null

        grailsApplication.domainClasses.each { GrailsDomainClass domainClass ->
            try {
                if (!domainClass.isAbstract()) {
                    def domainObject = domainClass.clazz.build()
                    assert domainObject."${domainClass.identifier.name}"
                }
            }
            catch (Exception e) {
                caughtError = e
                successful = false
            }
        }
        if (!successful) {
            throw new Exception("Caught exception while building all domain classes", caughtError)
        }
    }
}
