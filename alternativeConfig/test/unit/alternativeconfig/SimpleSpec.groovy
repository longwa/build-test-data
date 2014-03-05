package alternativeconfig
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.util.Holders
import spock.lang.Specification
/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Simple)
@Build(Simple)
class SimpleSpec extends Specification {

    void "test that build is done with the alternative config file"() {
        expect:
        Holders.config.grails.buildtestdata.testDataConfig == "AlternativeTestDataConfig"

        and:
        Simple.build().name == "Alternative name"
    }
}
