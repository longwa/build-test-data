package list

import bookstore.EstablishedAuthor
import grails.buildtestdata.BuildDataUnitTest
import spock.lang.Specification

class EstablishedAuthorUnitTests extends Specification implements BuildDataUnitTest {
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
        establishedAuthor.metaData.tags != null
        establishedAuthor.metaData.tags.size() == 2
        establishedAuthor.id > 0
    }
}
