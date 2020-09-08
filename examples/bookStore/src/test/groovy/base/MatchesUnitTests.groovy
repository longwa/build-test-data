package base

import bookstore.Invoice
import grails.buildtestdata.BuildDataUnitTest
import spock.lang.Specification

class MatchesUnitTests extends Specification implements BuildDataUnitTest {
    void setupSpec() {
        mockDomains(Invoice)
    }

    void testBuildNoArgs() {
        when:
        def invoice = build(Invoice)

        then:
        assert invoice != null
        assert invoice.departmentCode.matches("[A-Z]{2}[0-9]{4}")
    }

    void testBuildWithMatching() {
        when:
        def invoice = build(Invoice, [departmentCode: "AA1234"])

        then:
        assert invoice != null
        assert invoice.departmentCode == "AA1234"
    }

    void testBuildWithNonMatchingArg() {
        when:
        build(Invoice, [departmentCode: "Hmmmm?"])

        then:
        thrown(RuntimeException)
    }
}
