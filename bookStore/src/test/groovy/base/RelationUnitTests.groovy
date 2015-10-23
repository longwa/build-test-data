package base

import bookstore.Author
import bookstore.Book
import bookstore.Invoice
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import human.Arm
import human.Face
import magazine.Issue
import magazine.Page
import grails.buildtestdata.mixin.Build
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
@Build([Face, Book, Invoice, Page, Arm])
class RelationUnitTests {
    @Test
    void testOneToOneCascades() {
        def domainObject = Face.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.nose
        assert domainObject.nose.id
    }

    @Test
    void testBelongsToGetsHasMany() {
        def bookDomain = new DefaultGrailsDomainClass(Book)
        def domainProp = bookDomain.properties.find {it.name == 'author' }
        assert domainProp.isManyToOne()

        def domainObject = Book.build()
        assert domainObject
        assert domainObject.author
        assert domainObject.author.books
        assert domainObject.author.books.find {book ->
            book == domainObject
        }
        assert domainObject.id
    }

    @Test
    void testHasManyNullableFalse() {
        def authorDomain = new DefaultGrailsDomainClass(Author)
        def domainProp = authorDomain.properties.find {it.name == 'books' }
        assert domainProp.isOneToMany()
        assert !domainProp.isOptional()

        def bookConstrainedProperty = authorDomain.constrainedProperties['books']
        assert bookConstrainedProperty.isNullable() == false

        def domainObject = Author.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.books
        assert domainObject.address
        assert 2 == domainObject.books.size()
        domainObject.books.each {book ->
            assert domainObject == book.author
        }
    }

    @Test
    void testManyToManyNullableFalse() {
        def invoiceDomain = new DefaultGrailsDomainClass(Invoice)
        def domainProp = invoiceDomain.properties.find {it.name == 'books' }
        assert domainProp.isManyToMany()
        assert !domainProp.isOptional()

        def bookConstrainedProperty = invoiceDomain.constrainedProperties['books']
        assert bookConstrainedProperty.isNullable() == false

        def domainObject = Invoice.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.books
        assert 3 == domainObject.books.size()
    }

    @Test
    void testParentCollectionUpdatedWhenChildAutomaticallyAdded() {
        def page = Page.build()
        assert page
        assert page.issue
        assert [page.id] == page.issue.pages.id
    }

    @Test
    void testParentCollectionUpdatedWhenChildManuallyAdded() {
        def issue = new Issue(title: "title").save(failOnError: true)
        assert issue

        def page = Page.build(issue: issue)

        assert page
        assert page.issue == issue

        assert [page.id] == issue.pages.id
    }

    @Test
	void testHasOne() {
		def arm = Arm.build()
		def hand = arm.hand
		assert arm
		assert hand
		assert hand.arm == arm
		assert arm.hand == hand
	}
}
