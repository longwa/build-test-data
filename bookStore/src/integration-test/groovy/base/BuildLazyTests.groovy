package base

import bookstore.Author
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback

@Rollback
@TestMixin(IntegrationTestMixin)
class BuildLazyTests {
    void testBuildLazyNoParamsCreatesNewWhenNoneExist() {
        assert Author.count() == 0

        def domainObject = Author.buildLazy()
        assert domainObject
        assert Author.count() == 1
    }

    void testBuildLazyNoParamsFindsExistingWithoutCreateNew() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        def domainObject = Author.buildLazy()
        assert domainObject
        assert domainObject.firstName == "Foo"
        assert domainObject.lastName == "Qux"
        assert Author.count() == 1
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
