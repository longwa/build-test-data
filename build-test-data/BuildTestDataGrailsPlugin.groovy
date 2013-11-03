import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.DomainUtil
import grails.buildtestdata.TestDataConfigurationHolder
import groovy.util.logging.Commons

@Commons
class BuildTestDataGrailsPlugin {
    def version = "2.0.7"
    def grailsVersion = "2.0.0 > *"
    def loadAfter = ['services', 'dataSource', 'hibernate', 'hibernate4', 'validation']
    def watchedResources = ["file:./grails-app/domain/**.groovy"]

    def title = "Build Test Data Plugin"
    def description = '''\
Enables the easy creation of test data by automatic inspection of constraints.  Any properties that are required have
their constraints examined and a value is automatically provided for them.
'''

    def license = "APACHE"

    def developers = [
            [ name: "Ted Naleid", email: "contact@naleid.com" ],
            [ name: "Joe Hoover" ],
            [ name: "Matt Sheehan" ],
            [ name: "Aaron Long" ]
    ]

    def documentation = "http://bitbucket.org/tednaleid/grails-test-data/wiki/Home"

    def issueManagement = [ system: 'bitbucket', url: 'https://bitbucket.org/tednaleid/grails-test-data/issues' ]

    def doWithApplicationContext = { appCtx ->
        DomainUtil.setApplication(appCtx.grailsApplication)
        decorateDomainClasses(application.domainClasses)
    }

    def onChange = { event ->
        decorateDomainClasses(application.domainClasses)
    }

    private decorateDomainClasses(domainClasses) {
        def buildTestDataService = new BuildTestDataService()

        log.debug("Loading config file (if present)")
        TestDataConfigurationHolder.loadTestDataConfig()

        if (!TestDataConfigurationHolder.isPluginEnabled()) {
            log.warn("build-test-data plugin is disabled in this environment")
        }

        log.debug('build-test-data plugin enabled, decorating domain classes with build methods')

        domainClasses.each { domainClass ->
            log.debug("decorating $domainClass with build-test-data 'build' methods")
            try {
                buildTestDataService.decorateWithMethods(domainClass)
            }
            catch (Exception e) {
                log.error "Error decorating ${domainClass}. Message: [${e.getMessage()}]"
            }
        }

        log.debug("done decorating domain classes with 'build' methods")
    }
}
