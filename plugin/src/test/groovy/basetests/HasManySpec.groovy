package basetests

import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.UnitTestDataBuilder
import grails.buildtestdata.UnitTestDomainBuilder
import spock.lang.Specification

class HasManySpec extends Specification implements UnitTestDomainBuilder<AuthorHasMany> {

    void testHasManyNullableFalse() {
        when:
        def entity = AuthorHasMany.build()

        then:
        assert entity
        assert entity.id
        assert entity.books
        assert 1 == entity.books.size()
    }

}

@grails.persistence.Entity
class AuthorHasMany {
    //String firstName
    static hasMany = [books: BookToOne]

    static constraints = {
        books nullable: false
    }
}

@grails.persistence.Entity
class BookToOne {
    //String title
    static belongsTo = [author: AuthorHasMany]
}
