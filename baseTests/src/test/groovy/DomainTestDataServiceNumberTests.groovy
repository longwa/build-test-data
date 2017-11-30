import org.junit.Test

class DomainTestDataServiceNumberTests implements DomainTestDataServiceBase {
    def fields = """
                Long id
                Long version
                Integer testInteger
                Long testLong
                int testInt
                short testShort
                Short testShortObject
                byte testByte
                Byte testByteObject

    """

    @Test
    void testNumberManuallySetValuesOk() {
        def value = 1
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain {
               $fields
           }
        """)

		def domainObject = build(domainClass, [
                testInteger: value,
                testLong: value,
                testInt: value,
                testShort: value,
                testShortObject: value,
                testByte: value,
                testByteObject: value
        ])
        assertFieldsEqual(domainObject, value)
    }

    @Test
    void testNumberManuallySetGroovyTruthFalseValuesOk() {
        def validValueButGroovyTruthFalse = 0
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain {
               $fields
           }
        """)

		def domainObject = build(domainClass, [
                testInteger: validValueButGroovyTruthFalse,
                testLong: validValueButGroovyTruthFalse,
                testInt: validValueButGroovyTruthFalse,
                testShort: validValueButGroovyTruthFalse,
                testShortObject: validValueButGroovyTruthFalse,
                testByte: validValueButGroovyTruthFalse,
                testByteObject: validValueButGroovyTruthFalse
        ])
        assertFieldsEqual(domainObject, validValueButGroovyTruthFalse)
    }

    @Test
    void testNumberDefaultValueGroovyTruthFalseOk() {
        def defaultJavaValueButGroovyTruthFalse = 0
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNumberDefaultDomain {
               $fields
           }
        """)

		def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, defaultJavaValueButGroovyTruthFalse)
    }

    @Test
    void testNumberMin() {
        def minSize = 100
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNumberMinDomain {
               $fields

                static constraints = {
                    testInteger(min: $minSize)
                    testLong(min: ${minSize}L)
                    testInt(min: $minSize)
                    testShort(min: (short)$minSize)
                    testShortObject(min: (Short)$minSize)
                    testByte(min: (byte)$minSize)
                    testByteObject(min: (Byte)$minSize)
                }
           }
        """)

		def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, minSize)
    }

    @Test
    void testNumberMax() {
        def maxSize = -100
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNumberMaxDomain {
                $fields

                static constraints = {
                    testInteger(max: $maxSize)
                    testLong(max: ${maxSize}L)
                    testInt(max: $maxSize)
                    testShort(max: (short)($maxSize))
                    testShortObject(max: (Short)($maxSize))
                    testByte(max: (byte)($maxSize))
                    testByteObject(max: (Byte)($maxSize))
                }
           }
        """)

		def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, maxSize)
    }

    @Test
    void testNumberInList() {
        def numberOne = 100
        def numberTwo = 5
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNumberInListDomain {
                $fields

                static constraints = {
                    testInteger(inList: [$numberOne, $numberTwo])
                    testLong(inList: [${numberOne}L, ${numberTwo}L])
                    testInt(inList: [$numberOne, $numberTwo])
                    testShort(inList: [(short)$numberOne, (short)$numberTwo])
                    testShortObject(inList: [(Short)${numberOne}, (Short)${numberTwo}])
                    testByte(inList: [(byte)$numberOne, (byte)$numberTwo])
                    testByteObject(inList: [(Byte)$numberOne, (Byte)$numberTwo])
                }
           }
        """)

		def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, numberOne)
    }

    @Test
    void testNumberInRange() {
        def numberOne = 25
        def numberTwo = 35
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestNumberInRangeDomain {
                $fields

                static constraints = {
                    testInteger(range: $numberOne..$numberTwo)
                    testLong(range: ${numberOne}L..${numberTwo}L)
                    testInt(range: $numberOne..$numberTwo)
                    testShort(range: ((short)$numberOne)..((short)$numberTwo))
                    testShortObject(range: ((Short)${numberOne})..((Short)${numberTwo}))
                    testByte(range: ((byte)$numberOne)..((byte)$numberTwo))
                    testByteObject(range: ((Byte)$numberOne)..((Byte)$numberTwo))
                }
           }
        """)

		def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, numberOne)
    }

    static def assertFieldsEqual(domainObject, val) {
		assert val == domainObject.testInteger
		assert val == domainObject.testLong
		assert val == domainObject.testInt
		assert val == domainObject.testShort
		assert val == domainObject.testShortObject
		assert val == domainObject.testByte
		assert val == domainObject.testByteObject
    }    
}
