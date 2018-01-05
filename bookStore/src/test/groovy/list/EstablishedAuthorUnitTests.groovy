package list

import bookstore.EstablishedAuthor
import grails.buildtestdata.UnitTestDataBuilder
import grails.testing.gorm.DataTest
import org.junit.Test
import spock.lang.Specification

class EstablishedAuthorUnitTests extends Specification implements UnitTestDataBuilder {
    @Override
    Class[] getDomainClassesToMock() {
        [EstablishedAuthor]
    }

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
