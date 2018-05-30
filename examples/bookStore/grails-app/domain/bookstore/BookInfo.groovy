package bookstore

import enums.BookType

class BookInfo {
    BookType primaryType

    // Basic collections, one of Enum type which is even more special
    static hasMany = [bookTypes: BookType, alternateNames: String]

    static constraints = {
        bookTypes(nullable: false, minSize: 1)
        alternateNames(nullable: false, minSize: 1)
    }
}
