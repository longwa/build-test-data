package testdataconfig

import grails.buildtestdata.TestDataConfigurationHolder as TDHC
import spock.lang.Specification

class TestDataConfigurationHolderTests extends Specification {
    void testSampleDataStaticValue() {
        when:
        TDHC.setSampleData([TestDomain: [testProperty: 'ABC']])

        then:
        'ABC' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testSampleDataClosureValue() {
        given:
        def i = 0
        def propValues = ['foo', 'bar', 'baz']

        when:
        TDHC.setSampleData(TestDomain: [testProperty: { ->
            return "${propValues[i % propValues.size()]}${i++}"
        }])

        then:
        "foo0" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        "bar1" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        "baz2" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        "foo3" == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testGetSuppliedPropertyValue() {
        when:
        TDHC.setSampleData([TestDomain: [
            firstProperty : { -> 'closure1' },
            secondProperty: { values -> values.firstProperty + 'andthensome' }
        ]])

        then:
        'closure1' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'firstProperty')
        'closure1andthensome' == TDHC.getSuppliedPropertyValue([firstProperty: 'closure1'], 'TestDomain', 'secondProperty')
    }

    void testMergeAdditionalConfiguration() {
        given:
        TDHC.setSampleData([TestDomain: [testProperty: 'ABC']])

        when:
        TDHC.mergeConfig({ ->
            testDataConfig {
                sampleData {
                    'OtherDomain' {
                        testProperty = 'XYZ'
                    }
                }
            }
        })

        then:
        'ABC' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
        'XYZ' == TDHC.getSuppliedPropertyValue([:], 'OtherDomain', 'testProperty')
    }

    void testMergeAndOverwriteConfiguration() {
        given:
        TDHC.setSampleData([('TestDomain'): [testProperty: 'ABC']])

        when:
        TDHC.mergeConfig({ ->
            testDataConfig {
                sampleData {
                    'TestDomain' {
                        testProperty = 'XYZ'
                    }
                }
            }
        })

        then:
        'XYZ' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')
    }

    void testMergeAndThenReset() {
        when:
        TDHC.mergeConfig({ ->
            testDataConfig {
                sampleData {
                    'TestDomain' {
                        testProperty = 'XYZ'
                    }
                }
            }
        })

        then:
        'XYZ' == TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')

        when:
        TDHC.reset()
        TDHC.getSuppliedPropertyValue([:], 'TestDomain', 'testProperty')

        then:
        thrown(IllegalArgumentException)
    }
}
