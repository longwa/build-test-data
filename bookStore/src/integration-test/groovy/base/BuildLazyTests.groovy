package base

import grails.test.*
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import bookstore.Author
import bookstore.Book
import bookstore.Invoice
import human.Face
import magazine.Page
import magazine.Issue

class BuildLazyTests extends GroovyTestCase {

    void testBuildLazyNoParamsCreatesNewWhenNoneExist() {
		assertEquals 0, Author.count()
		
        def domainObject = Author.buildLazy()
        assertNotNull domainObject
		assertEquals 1, Author.count()
    }

    void testBuildLazyNoParamsFindsExistingWithoutCreateNew() {
		Author.build(firstName: "Foo", lastName: "Qux")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy()
        assertNotNull domainObject
		assertEquals "Foo", domainObject.firstName
		assertEquals "Qux", domainObject.lastName
		assertEquals 1, Author.count()
    }

    void testBuildLazyWithParamsCreatesNewWhenNoneExist() {
		assertEquals 0, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Bar")
        assertNotNull domainObject
		assertEquals 1, Author.count()
    }

    void testBuildLazyWithParamsCreatesNewWhenNoMatchingExist() {
		Author.build(firstName: "Foo")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Bar")
        assertNotNull domainObject
		assertEquals 2, Author.count()
    }

    void testBuildLazyWithParamsFindsExistingWithoutCreateNew() {
		Author.build(firstName: "Foo", lastName: "Qux")
		assertEquals 1, Author.count()
		
        def domainObject = Author.buildLazy(firstName: "Foo")
        assertNotNull domainObject
		assertEquals "Foo", domainObject.firstName
		assertEquals "Qux", domainObject.lastName
		assertEquals 1, Author.count()
    }

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
