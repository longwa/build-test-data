package base

import bookstore.Author
import bookstore.Book
import bookstore.Invoice
import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.mixin.Build
import human.Arm
import human.Face
import magazine.Issue
import magazine.Page
import spock.lang.Specification

@Build([Arm, Face, Book, Author, Invoice, Page])
class RelationUnitTestsUsingBuild extends Specification implements BuildDataUnitTest {
    void testOneToOneCascades() {
        when:
        def domainObject = Face.build()

        then:
        assert domainObject
        assert domainObject.id
        assert domainObject.nose
        assert domainObject.nose.id
    }

    void testBelongsToGetsHasMany() {
        when:
        def domainObject = Book.build()

        then:
        assert domainObject
        assert domainObject.author
        assert domainObject.author.books
        assert domainObject.author.books.find { book ->
            book == domainObject
        }
        assert domainObject.id
    }

    void testHasManyNullableFalse() {
        when:
        def domainObject = Author.build()

        then:
        assert domainObject
        assert domainObject.id
        assert domainObject.books
        assert domainObject.address
        assert 2 == domainObject.books.size()
        domainObject.books.each { book ->
            assert domainObject == book.author
        }
    }

    void testManyToManyNullableFalse() {
        when:
        def domainObject = Invoice.build()

        then:
        assert domainObject
        assert domainObject.id
        assert domainObject.books
        assert 3 == domainObject.books.size()
    }

    void testParentCollectionUpdatedWhenChildAutomaticallyAdded() {
        when:
        def page = Page.build()

        then:
        assert page
        assert page.issue
        assert [page.id] == page.issue.pages.id
    }

    void testParentCollectionUpdatedWhenChildManuallyAdded() {
        def issue = new Issue(title: "title").save(failOnError: true)
        assert issue

        when:
        def page = Page.build(issue: issue)

        then:
        assert page
        assert page.issue == issue
        assert [page.id] == issue.pages.id
    }

    void testHasOne() {
        when:
        def arm = Arm.build()
        def hand = arm.hand

        then:
        assert arm
        assert hand
        assert hand.arm == arm
        assert arm.hand == hand
    }
}
