package bookstore

class Invoice {
    String cardNumber
    String departmentCode
    
    static hasMany = [books: Book]
    static belongsTo = [Customer, Book]

    // Minimum order of 3, heh
    static constraints = {
        cardNumber(creditCard:true)
        books(nullable: false, minSize: 3)
        departmentCode(matches: '[A-Z]{2}[0-9]{4}')
    }
}
