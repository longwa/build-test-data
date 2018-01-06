package bookstore

class Payment {
    Date paid
    Float total

    static belongsTo = [invoice: Invoice]

}
