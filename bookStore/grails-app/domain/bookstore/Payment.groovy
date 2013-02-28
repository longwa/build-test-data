package bookstore

import bookstore.Invoice

class Payment {
    Date paid
    Float total

    static belongsTo = [invoice: Invoice]
    
}
