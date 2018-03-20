package bookstore

class EstablishedAuthor {

    String name
    Set<Book> hardcoverBooks
    List<Book> paperbackBooks
    Map metaData

    static hasMany = [hardcoverBooks: Book, paperbackBooks: Book]

    static constraints = {
        hardcoverBooks(nullable: false, minSize: 1)
        paperbackBooks(nullable: false, minSize: 1)
        metaData nullable: false
    }
}
