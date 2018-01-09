package base

import bookstore.Author
import bookstore.Book
import bookstore.Invoice
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import human.Arm
import human.Face
import magazine.Issue
import magazine.Page
import org.grails.core.DefaultGrailsDomainClass
import spock.lang.Specification

@Rollback
@Integration
class RelationTests extends Specification implements TestDataBuilder {
    void testOneToOneCascades() {
        when:
        def domainObject = build(Face)

        then:
        domainObject
        domainObject.id
        domainObject.nose
        domainObject.nose.id
    }

    void testBelongsToGetsHasMany() {
        when:
        def domainObject = build(Book)

        then:
        domainObject
        domainObject.author
        domainObject.author.books
        domainObject.author.books.find { book -> book == domainObject }
        domainObject.id
    }

    void testHasManyNullableFalse() {
        when:
        def domainObject = build(Author)

        then:
        domainObject
        domainObject.id
        domainObject.books
        domainObject.address
        2 == domainObject.books.size()
        domainObject.books.each { book -> assert domainObject == book.author }
    }

    void testManyToManyNullableFalse() {
        when:
        def domainObject = build(Invoice)
        then:
        domainObject
        domainObject.id
        domainObject.books
        3 == domainObject.books.size()
    }

    void testParentCollectionUpdatedWhenChildAutomaticallyAdded() {
        when:
        def page = build(Page)

        then:
        page
        page.issue
        [page.id] == page.issue.pages.id
    }

    void testParentCollectionUpdatedWhenChildManuallyAdded() {
        def issue = new Issue(title: "title").save(failOnError: true)
        assert issue

        when:
        def page = build(Page, [issue: issue])

        then:
        page
        page.issue == issue
        [page.id] == issue.pages.id
    }

    void testHasOne() {
        when:
        def arm = build(Arm)

        then:
        def hand = arm.hand
        assert arm
        assert hand
        assert hand.arm == arm
        assert arm.hand == hand
    }
}
