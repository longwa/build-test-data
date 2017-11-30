package base

import grails.buildtestdata.TestDataBuilder
import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.core.artefact.DomainClassArtefactHandler
import org.junit.Test

@Rollback
@Integration
class BuildAllTests implements TestDataBuilder {
    GrailsApplication grailsApplication

    @Test
    void testBuildAllDomains() {
        boolean successful = true
        Exception caughtError = null

        grailsApplication.getArtefacts(DomainClassArtefactHandler.TYPE).each { GrailsClass domainClass ->
            try {
                if (!domainClass.isAbstract()) {
                    def domainObject = build(domainClass.clazz)
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
