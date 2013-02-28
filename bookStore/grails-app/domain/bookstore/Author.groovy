package bookstore
class Author {
    String firstName
    String middleInitial
    String lastName
    Date dateOfBirth
    String gender
    Address address
 
    static hasMany = [books: Book]

    static constraints = {
        firstName(nullable: false, minSize: 1, maxSize: 25, blank: false)
        middleInitial(nullable: true, maxSize: 1)
        lastName(nullable: false, minSize: 1, maxSize: 35, blank: false)
        gender(blank: false, nullable: false, maxSize: 6, inList: ['male', 'female'])
        address(nullable: false)
        books(nullable: false, minSize: 2)
    }


}
