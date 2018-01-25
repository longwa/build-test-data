package basetests

import grails.buildtestdata.BuildDomainTest
import spock.lang.Specification

class BelongsToSpec extends Specification implements BuildDomainTest<BookBelongsTo> {

    void "Book with BelongsTo"() {
        when:
        def entity = BookBelongsTo.build()

        then:
        assert entity
        assert entity.id
        assert entity.author.name
    }

    void "ext works"() {
        when:
        mockDomain(AuthorWithExt)
        def entity = AuthorWithExt.build()

        then:
        assert entity
        assert entity.id
        assert entity.ext.author
    }

}

@grails.persistence.Entity
class AuthorHasBook {
    String name
}

@grails.persistence.Entity
class BookBelongsTo {
    static belongsTo = [author: AuthorHasBook]
}

@grails.persistence.Entity
class AuthorWithExt {
    AuthorExt ext
}

@grails.persistence.Entity
class AuthorExt {
    static belongsTo = [author: AuthorWithExt]
}