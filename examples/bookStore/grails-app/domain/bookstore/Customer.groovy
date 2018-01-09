package bookstore
class Customer {
    String firstName
    String middleInitial
    String lastName
    Address address

    static hasMany = [invoices: Invoice]

    static constraints = {
        firstName(nullable: false, minSize: 1, maxSize: 25, blank: false)
        middleInitial(nullable: true, maxSize: 1)
        lastName(nullable: false, minSize: 1, maxSize: 35, blank: false)
        address(nullable: true)
    }



}
