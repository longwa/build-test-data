package base

import bookstore.Author
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class BuildLazyTests extends Specification implements TestDataBuilder {

    void testBuildLazyNoParamsCreatesNewWhenNoneExist() {
        assert Author.count() == 0

        when:
        def domainObject = findOrBuild(Author)

        then:
        domainObject != null
        Author.count() == 1
    }

    void testBuildLazyNoParamsFindsExistingWithoutCreateNew() {
        build(Author, [firstName: "Foo", lastName: "Qux"])
        assert Author.count() == 1

        when:
        def domainObject = build(Author, find: true)

        then:
        domainObject
        domainObject.firstName == "Foo"
        domainObject.lastName == "Qux"
        Author.count() == 1
    }
    void "Test with new findOrBuild"() {
        assert Author.count() == 0

        when:
        def domainObject = Author.findOrBuild(firstName: "Bar")

        then:
        domainObject
        Author.count() == 1
    }

    void "Test build find with legacy buildLazy method"() {
        assert Author.count() == 0

        when:
        def domainObject = Author.findOrBuild(firstName: "Bar")

        then:
        domainObject
        Author.count() == 1
    }

    void "Test build lazy with specific id property given"() {
        def auth1 = build(Author, [firstName: "Foo", lastName: "Qux"])
        build(Author, [firstName: "Bar", lastName: "Baz"])

        when:
        def domainObject = Author.findOrBuild(id: auth1.id)

        then:
        domainObject
        domainObject.id == auth1.id
    }

    void testBuildLazyWithParamsCreatesNewWhenNoMatchingExist() {
        Author.build(firstName: "Foo")
        assert Author.count() == 1

        when:
        def domainObject = Author.findOrBuild(firstName: "Bar")

        then:
        domainObject
        Author.count() == 2
    }

    void testBuildLazyWithParamsFindsExistingWithoutCreateNew() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        when:
        def domainObject = Author.findOrBuild(firstName: "Foo")

        then:
        domainObject
        domainObject.firstName == "Foo"
        domainObject.lastName == "Qux"
        Author.count() == 1
    }

    void testBuildLazyWithParamsCreatesNewWithOnlyPartialMatch() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        when:
        def domainObject = Author.findOrBuild(firstName: "Foo", lastName: "Bar")

        then:
        domainObject
        domainObject.firstName == "Foo"
        domainObject.lastName == "Bar"
        Author.count() == 2
    }
}
