package list

import bookstore.EstablishedAuthor

import org.junit.Test


class EstablishedAuthorUnitTests {

    @Test
    void testRequiredListAndSetOk() {
        EstablishedAuthor establishedAuthor = EstablishedAuthor.build(name: "Steven King")
        assert establishedAuthor.hardcoverBooks != null
        assert establishedAuthor.hardcoverBooks.size() == 1
        assert establishedAuthor.paperbackBooks != null
        assert establishedAuthor.paperbackBooks.size() == 1
        assert establishedAuthor.id > 0
    }
}
