package grails.buildtestdata

import groovy.util.logging.Commons

@Commons
@SuppressWarnings("GroovyUnusedDeclaration")
class BuildTestDataGrailsPlugin {
    def grailsVersion = "3.0.0 > *"
    def loadAfter = ['services', 'dataSource', 'hibernate', 'hibernate4', 'validation']
    def watchedResources = ["file:./grails-app/domain/**.groovy"]
    def title = "Build Test Data Plugin"
    def description = 'Enables the easy creation of test data by automatically satisfying most constraints.'
    def license = "APACHE"

    def developers = [
        [name: "Aaron Long", email: "aaron@aaronlong.me"],
        [name: "Ted Naleid", email: "contact@naleid.com"],
        [name: "Søren Berg Glasius", email: "soeren@glasius.dk"],
        [name: "Joe Hoover"],
        [name: "Matt Sheehan"],
        [name: "Gregor Petrin"],
        [name: "Michael Cameron"],
        [name: "Jaime Garcia"]
    ]

    def documentation = "https://github.com/longwa/build-test-data/wiki"
    def issueManagement = [system: 'github', url: 'https://github.com/longwa/build-test-data/issues']
    def scm = [url: 'https://github.com/longwa/build-test-data/']

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
