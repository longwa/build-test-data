package base

import config.Hotel
import grails.buildtestdata.TestDataConfigurationHolder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback

@Rollback
@Integration
class TestDataConfigTests {
    void tearDown() {
        // we should reset the config holder when feeding it values in tests as it could cause issues
        // for other tests later on that are expecting the default config if we do not 
        TestDataConfigurationHolder.reset()
    }

    void testRealConfigFile() {
        // uses the file in grails-app/conf/TestDataConfig.groovy
        TestDataConfigurationHolder.reset() // you can reset it if you want it to get to a known value
        Hotel testHotel = Hotel.build()
        assert testHotel.name == "Motel 6"
        assert "6125551111", testHotel.faxNumber.startsWith("612555") // 
    }

    void testStaticValue() {
        def hotelNameAlwaysHilton = [('config.Hotel'): [name: "Hilton"]]
        TestDataConfigurationHolder.sampleData = hotelNameAlwaysHilton

        def hilton = Hotel.build()
        assert hilton.name == "Hilton"

        def stillHilton = Hotel.build()
        assert stillHilton.name == "Hilton"
    }

    void testConfigClosure() {
        def i = 0
        def hotelNameAlternates = [
            ('config.Hotel'): [name: { ->
                i++ % 2 == 0 ? "Holiday Inn" : "Hilton"
            }]
        ]
        TestDataConfigurationHolder.sampleData = hotelNameAlternates

        def holidayInn = Hotel.build()
        assert holidayInn.name == "Holiday Inn"

        def hilton = Hotel.build()
        assert hilton.name == "Hilton"

        def backToHolidayInn = Hotel.build()
        assert backToHolidayInn.name == "Holiday Inn"
    }

    void testConfigClosureWithPassedInValues() {
        TestDataConfigurationHolder.sampleData = [
            ('config.Hotel'): [faxNumber: { values ->
                "Fax number for $values.name"
            }]
        ]
        def holidayInn = Hotel.build(name: "Holiday Inn")
        assert holidayInn.faxNumber == "Fax number for Holiday Inn"
    }

    void testConfigClosureWithPassedInValuesFromEarlierClosure() {
        def i = 0
        TestDataConfigurationHolder.sampleData = [
            ('config.Hotel'): [
                name: { ->
                    i++ % 2 == 0 ? "Holiday Inn" : "Hilton"
                },
                faxNumber: { values ->
                    "Fax number for $values.name"
                }
            ]
        ]
        def holidayInn = Hotel.build()
        assert holidayInn.faxNumber == "Fax number for Holiday Inn"

        def hilton = Hotel.build()
        assert hilton.faxNumber == "Fax number for Hilton"
    }
}
