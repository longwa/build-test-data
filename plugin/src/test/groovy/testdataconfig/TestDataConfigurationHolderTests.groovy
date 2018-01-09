package testdataconfig

import grails.buildtestdata.TestDataConfigurationHolder as TDHC
import spock.lang.Specification

class TestDataConfigurationHolderTests extends Specification {
    void testSampleDataStaticValue() {
        when:
        TDHC.setSampleData([TestDomain:[testProperty:'ABC']])

        then:
        'ABC' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testSampleDataClosureValue() {
        given:
        def i = 0
        def propValues = ['foo', 'bar', 'baz']

        when:
        TDHC.setSampleData(TestDomain: [testProperty: {->
            return "${propValues[ i % propValues.size() ]}${i++}"
        }])

        then:
        "foo0" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        "bar1" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        "baz2" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        "foo3" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testGetSuppliedPropertyValue() {
        when:
        TDHC.setSampleData([TestDomain:[
                firstProperty:{ -> 'closure1' },
                secondProperty: { values -> values.firstProperty + 'andthensome' }
        ]])

        then:
        'closure1' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'firstProperty')
        'closure1andthensome' == TDHC.getSuppliedPropertyValue([firstProperty:  'closure1'], 'TestDomain', 'secondProperty')
    }
}
