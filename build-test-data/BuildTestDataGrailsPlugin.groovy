import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.TestDataConfigurationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass

class BuildTestDataGrailsPlugin {
    // the plugin version
    def version = "2.0.4"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.0 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    def loadAfter = ['services']

    def author = "Ted Naleid"
    def authorEmail = "contact@naleid.com"
    def title = "Build Test Data Plugin"
    def description = '''\\
Enables the easy creation of test data by automatic inspection of constraints.  Any properties that are required have
their constraints examined and a value is automatically provided for them.
'''

    def license = "APACHE"

    def developers = [
            [ name: "Ted Naleid" ],
            [ name: "Joe Hoover" ],
            [ name: "Matt Sheehan" ],
            [ name: "Aaron Long" ]
    ]

    def documentation = "http://bitbucket.org/tednaleid/grails-test-data/wiki/Home"

    def issueManagement = [ system: 'bitbucket', url: 'https://bitbucket.org/tednaleid/grails-test-data/issues' ]

    def doWithDynamicMethods = { applicationContext ->
        def buildTestDataService = new BuildTestDataService()

        log.debug("Loading config file (if present)")
        TestDataConfigurationHolder.loadTestDataConfig()

        if (TestDataConfigurationHolder.isPluginEnabled()) {
            log.debug('build-test-data plugin enabled, decorating domain classes with build methods')

            application.domainClasses.each { domainClass ->
                log.debug("decorating $domainClass with build-test-data 'build' methods")
                try {
                    buildTestDataService.decorateWithMethods(domainClass)
                } catch (Exception e) {
                    log.error "Error decorating ${domainClass}. Message: [${e.getMessage()}]"
                }
            }

            log.debug("done decorating domain classes with 'build' methods")
        } else {
            log.warn("build-test-data plugin is disabled in this environment")
        }
    }

    // If a domain class changes, re-decorate the newly loaded classes
    def onChange = { event ->
        def buildTestDataService = new BuildTestDataService()
        def domainClass = event.source

        if( domainClass instanceof GrailsDomainClass ) {
            log.debug("Loading config file (if present)")
            TestDataConfigurationHolder.loadTestDataConfig()

            if (TestDataConfigurationHolder.isPluginEnabled()) {
                log.debug("build-test-data plugin enabled, re-decorating domain class ${domainClass} with build methods")
                try {
                    buildTestDataService.decorateWithMethods(domainClass)
                } catch (Exception e) {
                    log.error("Error decorating ${domainClass}", e)
                }
            }
            else {
                log.warn("build-test-data plugin is disabled in this environment")
            }
        }
    }
}
