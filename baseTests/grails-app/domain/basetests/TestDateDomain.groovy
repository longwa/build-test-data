package basetests

class TestDateDomain {
    Long id
    Long version

    Date testDate
    Date testDateNull
    Date testMinDate
    Date testMaxDate
    Date testInListDate
    Date testRangeDate

    java.sql.Timestamp testTimestamp

    MonetaryValue monetaryValue

    static embedded = ['monetaryValue']

    static constraints = {
        testDateNull(nullable: true)
        testMinDate(min: new Date() + 100)
        testMaxDate(max: new Date() - 100)
        testInListDate(inList: [new Date() + 100, new Date() + 200])
        testRangeDate(range: new Date() + 100..new Date() + 200)
    }
}

class MonetaryValue {
    Currency currency
    BigDecimal amount
}
