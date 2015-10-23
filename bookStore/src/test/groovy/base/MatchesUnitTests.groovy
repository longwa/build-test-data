package base

import bookstore.Invoice
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(Invoice)
@Build(Invoice)
class MatchesUnitTests {
    void testBuildNoArgs() {
        def invoice = Invoice.build()
        assert invoice != null
        assert invoice.departmentCode.matches("[A-Z]{2}[0-9]{4}")
    }

    void testBuildWithMatching() {
        def invoice = Invoice.build(departmentCode: "AA1234")
        assert invoice != null
        assert invoice.departmentCode == "AA1234"
    }

    @Test(expected = Exception)
    void testBuildWithNonMatchingArg() {
        Invoice.build(departmentCode: "Hmmmm?")
    }
}
