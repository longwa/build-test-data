package base

import bookstore.Author
import config.Article
import config.Hotel
import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.GrailsUnitTestCase
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import org.junit.After

@TestMixin(GrailsUnitTestCase)
@Build(Hotel)
class TestDataConfigUnitTests {
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

    void testEagerLoad() {
        def hotel = Hotel.build()
        assert hotel.name == 'Motel 6'

        // Should also be able to build an article, even though we didn't
        // specifically include it and Hotel doesn't require it.
        def article = Article.build()
        assert article.name =~ 'Article'

        // Make sure we can use it
        assert Article.list().size() == 1
    }

    void testRecursiveEagerLoad() {
        def hotel = Hotel.build()
        assert hotel.name == 'Motel 6'

        // Should also be able to build an article, even though we didn't
        // specifically include it and Hotel doesn't require it.
        def article = Article.build()
        assert article.name =~ 'Article'

        // Make sure we can use it
        assert Article.list().size() == 1

        assert Author.build()
        assert Author.list().size() == 1
    }
}
