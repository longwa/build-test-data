package base

import bookstore.Author
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Test

@Rollback
@Integration
class BuildLazyTests {

    @Test
    void testBuildLazyNoParamsCreatesNewWhenNoneExist() {
        assert Author.count() == 0

        def domainObject = Author.buildLazy()
        assert domainObject
        assert Author.count() == 1
    }

    @Test
    void testBuildLazyNoParamsFindsExistingWithoutCreateNew() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        def domainObject = Author.buildLazy()
        assert domainObject
        assert domainObject.firstName == "Foo"
        assert domainObject.lastName == "Qux"
        assert Author.count() == 1
    }

    @Test
    void testBuildLazyWithParamsCreatesNewWhenNoneExist() {
        assert Author.count() == 0

        def domainObject = Author.buildLazy(firstName: "Bar")
        assert domainObject
        assert Author.count() == 1
    }

    @Test
    void testBuildLazyWithParamsCreatesNewWhenNoMatchingExist() {
        Author.build(firstName: "Foo")
        assert Author.count() == 1

        def domainObject = Author.buildLazy(firstName: "Bar")
        assert domainObject
        assert Author.count() == 2
    }

    @Test
    void testBuildLazyWithParamsFindsExistingWithoutCreateNew() {
        Author.build(firstName: "Foo", lastName: "Qux")
        assert Author.count() == 1

        def domainObject = Author.buildLazy(firstName: "Foo")
        assert domainObject
        assert domainObject.firstName == "Foo"
        assert domainObject.lastName == "Qux"
        assert Author.count() == 1
    }

    @Test
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
