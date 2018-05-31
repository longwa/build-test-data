package base

import bookstore.Author
import bookstore.Book
import config.Hotel
import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.spockframework.compiler.model.Spec
import spock.lang.Specification

@Rollback
@Integration
class TestDataConfigTests extends Specification implements TestDataBuilder {
    void cleanup() {
        // we should reset the config holder when feeding it values in tests as it could cause issues
        // for other tests later on that are expecting the default config if we do not 
        TestDataConfigurationHolder.reset()
    }

    void testRealConfigFile() {
        // uses the file in grails-app/conf/TestDataConfig.groovy
        TestDataConfigurationHolder.reset() // you can reset it if you want it to get to a known value

        when:
        Hotel testHotel = build(Hotel)
        then:
        testHotel.name == "Motel 6"
        testHotel.faxNumber.startsWith("612555")
    }

    void testStaticValue() {
        def hotelNameAlwaysHilton = [('config.Hotel'): [name: "Hilton"]]
        TestDataConfigurationHolder.sampleData = hotelNameAlwaysHilton

        when:
        def hilton = build(Hotel)
        then:
        assert hilton.name == "Hilton"

        when:
        def stillHilton = build(Hotel)

        then:
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

        when:
        def holidayInn = build(Hotel)
        then:
        holidayInn.name == "Holiday Inn"

        when:
        def hilton = build(Hotel)
        then:
        hilton.name == "Hilton"

        when:
        def backToHolidayInn = build(Hotel)
        then:
        backToHolidayInn.name == "Holiday Inn"
    }

    void testConfigClosureWithPassedInValues() {
        TestDataConfigurationHolder.sampleData = [
            ('config.Hotel'): [faxNumber: { values ->
                "Fax number for $values.name"
            }]
        ]
        when:
        def holidayInn = build(Hotel, [name: "Holiday Inn"])

        then:
        holidayInn.faxNumber == "Fax number for Holiday Inn"
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

        when:
        def holidayInn = build(Hotel)
        then:
        holidayInn.faxNumber == "Fax number for Holiday Inn"

        when:
        def hilton = build(Hotel)
        then:
        hilton.faxNumber == "Fax number for Hilton"
    }

    void testConfigClosureWithPassedInstanceValue() {
        TestDataConfigurationHolder.sampleData = [
            ('bookstore.Author'): [books: { values, obj ->
                [
                    build([save: false], Book, [author: obj, title: 'James']),
                    build([save: false], Book, [author: obj, title: 'Kevin'])
                ]
            }]
        ]
        when:
        def author = build(Author, [name: "Aaron"])

        then:
        author.books
        author.books.each {
            assert it.author == author
        }
        author.books.find { it.title == 'James' }
        author.books.find { it.title == 'Kevin' }
    }

    void cleanupSpec() {
        TestDataConfigurationHolder.reset()
    }
}
