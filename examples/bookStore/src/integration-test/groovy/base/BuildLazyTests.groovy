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
        def domainObject = buildLazy(Author)

        then:
        domainObject != null
        Author.count() == 1
    }

    void testBuildLazyNoParamsFindsExistingWithoutCreateNew() {
        build(Author, [firstName: "Foo", lastName: "Qux"])
        assert Author.count() == 1

        when:
        def domainObject = buildLazy(Author)

        then:
        domainObject
        domainObject.firstName == "Foo"
        domainObject.lastName == "Qux"
        Author.count() == 1
    }

    void testBuildLazyWithParamsCreatesNewWhenNoneExist() {
        assert Author.count() == 0

        def domainObject = Author.buildLazy(firstName: "Bar")
        assert domainObject
        assert Author.count() == 1
    }

    void testBuildLazyWithParamsCreatesNewWhenNoMatchingExist() {
        Author.build(firstName: "Foo")
        assert Author.count() == 1

        def domainObject = Author.buildLazy(firstName: "Bar")
        assert domainObject
        assert Author.count() == 2
    }

    void testBuildLazyWithParamsFindsExistingWithoutCreateNew() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        def domainObject = Author.buildLazy(firstName: "Foo")
        assert domainObject
        assert domainObject.firstName == "Foo"
        assert domainObject.lastName == "Qux"
        assert Author.count() == 1
    }

    void testBuildLazyWithParamsCreatesNewWithOnlyPartialMatch() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        def domainObject = Author.buildLazy(firstName: "Foo", lastName: "Bar")
        assert domainObject
        assert domainObject.firstName == "Foo"
        assert domainObject.lastName == "Bar"
        assert Author.count() == 2
    }
}
