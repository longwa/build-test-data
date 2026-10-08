package base

import bookstore.BookInfo
import enums.BookType
import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.TestDataConfigurationHolder
import spock.lang.Specification

class ToManyBasicTypeSpec extends Specification implements BuildDataUnitTest {
    void setup() {
        TestDataConfigurationHolder.reset()
    }

    @Override
    Class[] getDomainClassesToMock() {
        [BookInfo]
    }

    void "build simple required enum property"() {
        when:
        BookInfo b = BookInfo.build(alternateNames: ['foo'], bookTypes: [BookType.Fiction])

        then:
        b.primaryType in BookType.values()
    }

    void "build basic to-many String property"() {
        when:
        BookInfo b = BookInfo.build(primaryType: BookType.Fiction, bookTypes: [BookType.Fiction])

        then:
        b.alternateNames
    }

    void "build basic to-many Enum property"() {
        when:
        BookInfo b = BookInfo.build(primaryType: BookType.Fiction, alternateNames: ['foo'])

        then:
        b.bookTypes
    }
}

