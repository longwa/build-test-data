package basetests

import grails.buildtestdata.BuildDomainTest
import spock.lang.Specification

class HasManyMinSizeSpec extends Specification implements BuildDomainTest<AuthorHasManyMin> {

    void testHasManyNullableFalse() {
        when:
        def entity = AuthorHasManyMin.build()

        then:
        assert entity
        assert entity.id
        assert entity.books
        assert 2 == entity.books.size()
    }

}

@grails.persistence.Entity
class AuthorHasManyMin {
    static hasMany = [books: BookToOneMin]

    static constraints = {
        books nullable: false, minSize: 2
    }
}

@grails.persistence.Entity
class BookToOneMin {
    static belongsTo = [author: AuthorHasManyMin]
}
