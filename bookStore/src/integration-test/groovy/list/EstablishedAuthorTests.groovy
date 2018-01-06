package list

import bookstore.EstablishedAuthor
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class EstablishedAuthorTests extends Specification implements TestDataBuilder {
    void testRequiredListAndSetOk() {
        when:
        EstablishedAuthor establishedAuthor = build(EstablishedAuthor, [name: "Steven King"])
        then:
        establishedAuthor.hardcoverBooks != null
        establishedAuthor.hardcoverBooks.size() == 1
        establishedAuthor.paperbackBooks != null
        establishedAuthor.paperbackBooks.size() == 1
        establishedAuthor.id > 0
    }
}
