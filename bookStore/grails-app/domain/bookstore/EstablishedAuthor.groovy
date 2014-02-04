package bookstore

import bookstore.Book

class EstablishedAuthor {

    String name
    Set<Book> hardcoverBooks
    List<Book> paperbackBooks

    static hasMany = [hardcoverBooks: Book, paperbackBooks: Book]

    static constraints = {
        hardcoverBooks(nullable: false, minSize: 1)
        paperbackBooks(nullable: false, minSize: 1)
    }
}
