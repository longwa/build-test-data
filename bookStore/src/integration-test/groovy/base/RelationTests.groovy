package base

import bookstore.Author
import bookstore.Book
import bookstore.Invoice
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import human.Arm
import human.Face
import magazine.Issue
import magazine.Page
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Test

@Rollback
@Integration
class RelationTests {

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
        def domainObject = Book.build()
        assert domainObject
        assert domainObject.author
        assert domainObject.author.books
        assert domainObject.author.books.find { book ->
            book == domainObject
        }
        assert domainObject.id
    }

    @Test
    void testHasManyNullableFalse() {
        def domainObject = Author.build()
        assert domainObject
        assert domainObject.id
        assert domainObject.books
        assert domainObject.address
        assert 2 == domainObject.books.size()
        domainObject.books.each { book ->
            assert domainObject == book.author
        }
    }

    @Test
    void testManyToManyNullableFalse() {
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
