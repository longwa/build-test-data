package base

import bookstore.Author
import bookstore.Book
import bookstore.Invoice
import grails.buildtestdata.UnitTestDataBuilder
import grails.buildtestdata.mixin.Build
import human.Arm
import human.Face
import magazine.Issue
import magazine.Page

import spock.lang.Specification

class RelationUnitTests extends Specification implements UnitTestDataBuilder {
    void setupSpec() {
        mockDomains(Face, Book, Author, Invoice, Page, Issue, Arm)
    }

    void testOneToOneCascades() {
        when:
        def domainObject = build(Face)

        then:
        assert domainObject
        assert domainObject.id
        assert domainObject.nose
        assert domainObject.nose.id
    }

    void testBelongsToGetsHasMany() {
        when:
        def domainObject = build(Book)

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
        def domainObject = build(Author)

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
        def domainObject = build(Invoice)

        then:
        assert domainObject
        assert domainObject.id
        assert domainObject.books
        assert 3 == domainObject.books.size()
    }

    void testParentCollectionUpdatedWhenChildAutomaticallyAdded() {
        when:
        def page = build(Page)

        then:
        assert page
        assert page.issue
        assert [page.id] == page.issue.pages.id
    }

    void testParentCollectionUpdatedWhenChildManuallyAdded() {
        def issue = new Issue(title: "title").save(failOnError: true)
        assert issue

        when:
        def page = build(Page, [issue: issue])

        then:
        assert page
        assert page.issue == issue
        assert [page.id] == issue.pages.id
    }

    void testHasOne() {
        when:
        def arm = build(Arm)
        def hand = arm.hand

        then:
        assert arm
        assert hand
        assert hand.arm == arm
        assert arm.hand == hand
    }
}
