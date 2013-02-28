import grails.buildtestdata.TestDataConfigurationHolder as TDCH

class TestDataConfigurationHolderTests extends GroovyTestCase {

    void testSampleDataStaticValue() {
        TDCH.sampleData = [TestDomain:[testProperty:'ABC']]
        assertEquals 'ABC', TDCH.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testSampleDataClosureValue() {
        def i = 0
        def propValues = ['foo', 'bar', 'baz']
        TDCH.sampleData = [TestDomain: [testProperty: {->
            return "${propValues[ i % propValues.size() ]}${i++}"
        }]]

        assert "foo0" == TDCH.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        assert "bar1" == TDCH.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        assert "baz2" == TDCH.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        assert "foo3" == TDCH.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testGetSuppliedPropertyValue() {
        TDCH.sampleData = [TestDomain:[
                firstProperty:{ -> 'closure1' },
                secondProperty: { values -> values.firstProperty + 'andthensome' }
        ]]
        assertEquals 'closure1', TDCH.getSuppliedPropertyValue([:], 'TestDomain', 'firstProperty')
        assertEquals 'closure1andthensome', TDCH.getSuppliedPropertyValue([firstProperty:  'closure1'], 'TestDomain', 'secondProperty')
    }

}
