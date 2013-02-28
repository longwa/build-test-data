package bookstore

import bookstore.Book

class EstablishedAuthor {

    static hasMany = [books: Book]

    static constraints = {
        books(nullable: false, minSize: 5)
    }


}
