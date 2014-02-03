package list

import bookstore.Book
import bookstore.EstablishedAuthor
import grails.buildtestdata.mixin.Build
import org.junit.Test

class EstablishedAuthorTests {

    @Test
    void testRequiredListAndSetOk() {
        EstablishedAuthor establishedAuthor = EstablishedAuthor.build(name: "Steven King")
        assert establishedAuthor.hardcoverBooks != null
        assert establishedAuthor.hardcoverBooks.size() == 1
        assert establishedAuthor.paperbackBooks != null
        assert establishedAuthor.paperbackBooks.size() == 2
        assert establishedAuthor.id > 0
    }
}
