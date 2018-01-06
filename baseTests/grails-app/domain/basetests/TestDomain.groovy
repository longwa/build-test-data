package basetests

class TestDomain {
    Long id
    Long version

    String testProperty
    String testMinProperty
    String testMaxProperty
    String testInListProperty
    String testBlankProperty
    String testCCProperty
    String testEmailProperty
    String testURLProperty
    String testRangeProperty
    String testStringSizeProperty

    Boolean testBoolean
    Integer testInteger
    Byte[] testByteObject
    byte[] testBytePrimitive

    Boolean testBooleanNull
    Integer testIntegerNull
    Byte[] testByteObjectNull
    byte[] testBytePrimitiveNull

    static constraints = {
        testMinProperty(minSize: 200)
        testMaxProperty(maxSize: 2)
        testInListProperty(inList: ['one', 'two'])
        testBlankProperty(blank: false)
        testCCProperty(creditCard: true)
        testEmailProperty(email: true)
        testURLProperty(url: true)
        testRangeProperty(range: 'x'..'z')
        testStringSizeProperty(size: 1..3)

        testBooleanNull(nullable: true)
        testIntegerNull(nullable: true)
        testByteObjectNull(nullable: true)
        testBytePrimitiveNull(nullable: true)
    }
}
