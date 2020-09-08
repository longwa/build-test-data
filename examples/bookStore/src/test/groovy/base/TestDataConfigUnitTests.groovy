package base

import bookstore.Author
import config.Article
import config.Hotel
import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.TestDataConfigurationHolder
import spock.lang.Specification

class TestDataConfigUnitTests extends Specification implements BuildDataUnitTest {
    void cleanup() {
        // we should reset the config holder when feeding it values in tests as it could cause issues
        // for other tests later on that are expecting the default config if we do not
        TestDataConfigurationHolder.reset()
    }

    void testRealConfigFile() {
        mockDomains(Hotel)

        // uses the file in grails-app/conf/TestDataConfig.groovy
        TestDataConfigurationHolder.reset() // you can reset it if you want it to get to a known value

        when:
        Hotel testHotel = Hotel.build()

        then:
        "Motel 6" == testHotel.name
        testHotel.faxNumber.startsWith("612555")
    }

    void testStaticValue() {
        mockDomains(Hotel)

        def hotelNameAlwaysHilton = [('config.Hotel'): [name: "Hilton"]]
        TestDataConfigurationHolder.sampleData = hotelNameAlwaysHilton

        when:
        def hilton = Hotel.build()
        def stillHilton = Hotel.build()

        then:
        "Hilton" == hilton.name
        "Hilton" == stillHilton.name
    }

    void testConfigClosure() {
        mockDomains(Hotel)

        def i = 0
        def hotelNameAlternates = [
            ('config.Hotel'): [name: { ->
                i++ % 2 == 0 ? "Holiday Inn" : "Hilton"
            }]
        ]
        TestDataConfigurationHolder.sampleData = hotelNameAlternates

        when:
        def holidayInn = Hotel.build()
        def hilton = Hotel.build()
        def backToHolidayInn = Hotel.build()

        then:
        holidayInn.name == "Holiday Inn"
        hilton.name == "Hilton"
        backToHolidayInn.name == "Holiday Inn"
    }

    void testAdditionalBuild() {
        mockDomains(Hotel)

        when:
        def hotel = Hotel.build()

        then:
        hotel.name == 'Motel 6'

        // Should also be able to build an article, even though we didn't
        // specifically include it and Hotel doesn't require it.
        when:
        def article = Article.build()

        then:
        article.name =~ 'Article'

        // Make sure we can use it
        Article.list().size() == 1
    }

    void testRecursiveAdditionalBuild() {
        mockDomains(Hotel)

        when:
        def hotel = Hotel.build()

        then:
        hotel.name == 'Motel 6'

        // Should also be able to build an article, even though we didn't
        // specifically include it and Hotel doesn't require it.
        when:
        def article = Article.build()

        then:
        article.name =~ 'Article'

        // Make sure we can use it
        Article.list().size() == 1
        Author.build()
        Author.list().size() == 1
    }
}
