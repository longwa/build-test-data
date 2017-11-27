package grails.buildtestdata

import grails.plugins.Plugin
import groovy.transform.CompileStatic

@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
class BuildTestDataGrailsPlugin extends Plugin {
    def grailsVersion = "3.3.0 > *"

    def title = "Build Test Data Plugin"
    def description = 'Enables the easy creation of test data by automatically satisfying most constraints.'
    def license = "APACHE"
    def documentation = "https://github.com/longwa/build-test-data/wiki"

    def developers = [
        [name: "Aaron Long", email: "aaron@aaronlong.me"],
        [name: "Ted Naleid", email: "contact@naleid.com"],
    ]

    def issueManagement = [system: 'github', url: 'https://github.com/longwa/build-test-data/issues']
    def scm = [url: 'https://github.com/longwa/build-test-data/']
}
