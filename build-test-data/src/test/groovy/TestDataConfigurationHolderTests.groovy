import grails.buildtestdata.TestDataConfigurationHolder as TDHC
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

@TestMixin(GrailsUnitTestMixin)
class TestDataConfigurationHolderTests {
    void testSampleDataStaticValue() {
        TDHC.setSampleData([TestDomain:[testProperty:'ABC']])
        assert 'ABC' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testSampleDataClosureValue() {
        def i = 0
        def propValues = ['foo', 'bar', 'baz']
        TDHC.setSampleData(TestDomain: [testProperty: {->
            return "${propValues[ i % propValues.size() ]}${i++}"
        }])

        assert "foo0" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        assert "bar1" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        assert "baz2" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        assert "foo3" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testGetSuppliedPropertyValue() {
        TDHC.setSampleData([TestDomain:[
                firstProperty:{ -> 'closure1' },
                secondProperty: { values -> values.firstProperty + 'andthensome' }
        ]])

        assert 'closure1' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'firstProperty')
        assert 'closure1andthensome' == TDHC.getSuppliedPropertyValue([firstProperty:  'closure1'], 'TestDomain', 'secondProperty')
    }
}
