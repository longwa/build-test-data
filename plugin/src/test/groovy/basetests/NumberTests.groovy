package basetests

import spock.lang.Specification

// These tests may not really be valid, not sure how to dynamically register these entities in Grails 3
class NumberTests extends Specification implements DomainTestBase {
    def fields = """
                Long id
                Long version
                Integer testInteger
                Long testLong
                int testInt
                short testShort
                Short testShortObject
                BigDecimal testBigDecimal
                Double testDouble
                Float testFloat
                byte testByte
                Byte testByteObject

    """

    void testNumberManuallySetValuesOk() {
        expect:
        def value = 1
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain1 {
               $fields
           }
        """)


        def domainObject = build(domainClass, [
            testInteger    : value,
            testLong       : value,
            testInt        : value,
            testShort      : value as short,
            testShortObject: value as Short,
            testBigDecimal : value as BigDecimal,
            testDouble     : value as Double,
            testFloat      : value as Float,
            testByte       : value as byte,
            testByteObject : value as Byte
        ])
        assertFieldsEqual(domainObject, value)
    }

    void testNumberManuallySetGroovyTruthFalseValuesOk() {
        expect:
        def validValueButGroovyTruthFalse = 0
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain2 {
               $fields
           }
        """)

        def domainObject = build(domainClass, [
            testInteger    : validValueButGroovyTruthFalse,
            testLong       : validValueButGroovyTruthFalse,
            testInt        : validValueButGroovyTruthFalse,
            testShort      : validValueButGroovyTruthFalse as short,
            testShortObject: validValueButGroovyTruthFalse as Short,
            testBigDecimal : validValueButGroovyTruthFalse as BigDecimal,
            testDouble     : validValueButGroovyTruthFalse as Double,
            testFloat      : validValueButGroovyTruthFalse as Float,
            testByte       : validValueButGroovyTruthFalse as byte,
            testByteObject : validValueButGroovyTruthFalse as Byte,
        ])

        assertFieldsEqual(domainObject, validValueButGroovyTruthFalse)
    }

    void testNumberDefaultValueGroovyTruthFalseOk() {
        expect:
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

    void testNumberMin() {
        expect:
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
                    testBigDecimal(min: (BigDecimal)$minSize)
                    testDouble(min: (Double)$minSize)
                    testFloat(min: (Float)$minSize)
                    testByte(min: (byte)$minSize)
                    //testByteObject(min: (Byte)$minSize)
                }
           }
        """)

        def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, minSize)
    }

    void testNumberMax() {
        expect:
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
                    testBigDecimal(max: ($maxSize) as BigDecimal)
                    testDouble(max: ($maxSize) as Double)
                    testFloat(max: ($maxSize) as Float)
                    testByte(max: (byte)($maxSize))
                    //testByteObject(max: (Byte)($maxSize))
                }
           }
        """)

        def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, maxSize)
    }

    void testNumberInList() {
        expect:
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
                    testBigDecimal(inList: [(BigDecimal)${numberOne}, (BigDecimal)${numberTwo}])
                    testDouble(inList: [(Double)${numberOne}, (Double)${numberTwo}])
                    testFloat(inList: [(Float)${numberOne}, (Float)${numberTwo}])
                    testByte(inList: [(byte)$numberOne, (byte)$numberTwo])
                    //testByteObject(inList: [(Byte)$numberOne, (Byte)$numberTwo])
                }
           }
        """)

        def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, numberOne)
    }

    void testNumberInRange() {
        expect:
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
                    testBigDecimal(range: ((BigDecimal)${numberOne})..((BigDecimal)${numberTwo}))
                    testDouble(range: ((Double)${numberOne})..((Double)${numberTwo}))
                    testFloat(range: ((Float)${numberOne})..((Float)${numberTwo}))
                    testByte(range: ((byte)$numberOne)..((byte)$numberTwo))
                    //testByteObject(range: ((Byte)$numberOne)..((Byte)$numberTwo))
                }
           }
        """)

        def domainObject = build(domainClass)
        assertFieldsEqual(domainObject, numberOne)
    }

    static boolean assertFieldsEqual(domainObject, val) {
        assert val == domainObject.testInteger
        assert val == domainObject.testLong
        assert val == domainObject.testInt
        assert val == domainObject.testShort
        assert val == domainObject.testShortObject
        assert val == domainObject.testBigDecimal
        assert val == domainObject.testDouble
        assert val == domainObject.testFloat
        assert val == domainObject.testByte
        //assert val == domainObject.testByteObject
        return true
    }
}
