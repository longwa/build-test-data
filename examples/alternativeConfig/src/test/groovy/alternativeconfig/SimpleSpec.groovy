package alternativeconfig

import grails.buildtestdata.BuildDataUnitTest
import grails.util.Holders
import spock.lang.Specification

class SimpleSpec extends Specification implements BuildDataUnitTest {
    void setupSpec() {
        mockDomains(Simple)
    }

    void "test that build is done with the alternative config file"() {
        expect:
        Holders.config.grails.buildtestdata.testDataConfig == "AlternativeTestDataConfig"

        and:
        build(Simple).name == "Alternative name"
    }
}
