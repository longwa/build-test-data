class DomainTestDataServiceNumberTests extends DomainTestDataServiceBase {
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

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

    void testNumberManuallySetValuesOk() {
        def value = 1
        def domainClass = createDomainClass("""
            class TestDomain {
               $fields
           }
        """)

		def domainObject = domainClass.build(
                testInteger: value,
                testLong: value,
                testInt: value,
                testShort: value,
                testShortObject: value,
                testByte: value,
                testByteObject: value
        )
        assertFieldsEqual(domainObject, value)
    }

    void testNumberManuallySetGroovyTruthFalseValuesOk() {
        def validValueButGroovyTruthFalse = 0
        def domainClass = createDomainClass("""
            class TestDomain {
               $fields
           }
        """)

		def domainObject = domainClass.build(
                testInteger: validValueButGroovyTruthFalse,
                testLong: validValueButGroovyTruthFalse,
                testInt: validValueButGroovyTruthFalse,
                testShort: validValueButGroovyTruthFalse,
                testShortObject: validValueButGroovyTruthFalse,
                testByte: validValueButGroovyTruthFalse,
                testByteObject: validValueButGroovyTruthFalse
        )
        assertFieldsEqual(domainObject, validValueButGroovyTruthFalse)
    }

    void testNumberDefaultValueGroovyTruthFalseOk() {
        def defaultJavaValueButGroovyTruthFalse = 0
        def domainClass = createDomainClass("""
            class TestDomain {
               $fields
           }
        """)

		def domainObject = domainClass.build()
        assertFieldsEqual(domainObject, defaultJavaValueButGroovyTruthFalse)
    }

    void testNumberMin() {
        def minSize = 100
        def domainClass = createDomainClass("""
            class TestDomain {
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

		def domainObject = domainClass.build()
        assertFieldsEqual(domainObject, minSize)
    }

    void testNumberMax() {
        def maxSize = -100
        def domainClass = createDomainClass("""
            class TestDomain {
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

		def domainObject = domainClass.build()

        assertFieldsEqual(domainObject, maxSize)
    }

    void testNumberInList() {
        def numberOne = 100
        def numberTwo = 5
        def domainClass = createDomainClass("""
            class TestDomain {
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

		def domainObject = domainClass.build()
        assertFieldsEqual(domainObject, numberOne)
    }

    void testNumberInRange() {
        def numberOne = 25
        def numberTwo = 35
        def domainClass = createDomainClass("""
            class TestDomain {
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

		def domainObject = domainClass.build()
        assertFieldsEqual(domainObject, numberOne)
    }

    def assertFieldsEqual(domainObject, val) {
		assertEquals val, domainObject.testInteger
		assertEquals val, domainObject.testLong
		assertEquals val, domainObject.testInt
		assertEquals val, domainObject.testShort
		assertEquals val, domainObject.testShortObject
		assertEquals val, domainObject.testByte
		assertEquals val, domainObject.testByteObject
    }    
}
