package base

import config.Hotel
import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.GrailsUnitTestCase
import grails.buildtestdata.mixin.Build
import org.junit.After

@Build(Hotel)
class TestDataConfigUnitTests {

    def buildTestDataService

    @After
    void tearDown() {
        // we should reset the config holder when feeding it values in tests as it could cause issues
        // for other tests later on that are expecting the default config if we do not 
        TestDataConfigurationHolder.reset()
    }

    void testRealConfigFile() {
        // uses the file in grails-app/conf/TestDataConfig.groovy
        TestDataConfigurationHolder.reset() // you can reset it if you want it to get to a known value
        Hotel testHotel = Hotel.build()
        assertEquals "Motel 6", testHotel.name
        assert "6125551111", testHotel.faxNumber.startsWith("612555") // 
    }

    void testStaticValue() {
        def hotelNameAlwaysHilton = [('config.Hotel'): [name: "Hilton"]]
        TestDataConfigurationHolder.sampleData = hotelNameAlwaysHilton

        def hilton = Hotel.build()
        assertEquals "Hilton", hilton.name

        def stillHilton = Hotel.build()
        assertEquals "Hilton", stillHilton.name
    }

    void testConfigClosure() {
        def i = 0
        def hotelNameAlternates = [
                ('config.Hotel'): [name: {->
                    i++ % 2 == 0 ? "Holiday Inn" : "Hilton"
                }]
        ]
        TestDataConfigurationHolder.sampleData = hotelNameAlternates
        
        def holidayInn = Hotel.build()
        assertEquals "Holiday Inn", holidayInn.name

        def hilton = Hotel.build()
        assertEquals "Hilton", hilton.name

        def backToHolidayInn = Hotel.build()
        assertEquals "Holiday Inn", backToHolidayInn.name
    }
}
