package base

import bookstore.Author
import grails.buildtestdata.mixin.Build
import org.junit.Test

@Build(Author)
class BuildLazyUnitTests {


    @Test
    void testBuildLazyNoParamsCreatesNewWhenNoneExist() {
		assertEquals 0, Author.count()
		
        def domainObject = Author.buildLazy()
        assertNotNull domainObject
		assertEquals 1, Author.count()
    }

    @Test
    void testBuildLazyNoParamsFindsExistingWithoutCreateNew() {
		Author.build(firstName: "Foo", lastName: "Qux")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy()
        assertNotNull domainObject
		assertEquals "Foo", domainObject.firstName
		assertEquals "Qux", domainObject.lastName
		assertEquals 1, Author.count()
    }

    @Test
    void testBuildLazyWithParamsCreatesNewWhenNoneExist() {
		assertEquals 0, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Bar")
        assertNotNull domainObject
		assertEquals 1, Author.count()
    }

    @Test
    void testBuildLazyWithParamsCreatesNewWhenNoMatchingExist() {
		Author.build(firstName: "Foo")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Bar")
        assertNotNull domainObject
		assertEquals 2, Author.count()
    }

    @Test
    void testBuildLazyWithParamsFindsExistingWithoutCreateNew() {
		Author.build(firstName: "Foo", lastName: "Qux")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Foo")
        assertNotNull domainObject
		assertEquals "Foo", domainObject.firstName
		assertEquals "Qux", domainObject.lastName
		assertEquals 1, Author.count()
    }

    @Test
    void testBuildLazyWithParamsCreatesNewWithOnlyPartialMatch() {
		Author.build(firstName: "Foo", lastName: "Qux")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Foo", lastName: "Bar")
        assertNotNull domainObject
		assertEquals "Foo", domainObject.firstName
		assertEquals "Bar", domainObject.lastName
		assertEquals 2, Author.count()
    }
}
