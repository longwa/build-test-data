class DomainTestDataServiceByteTests extends DomainTestDataServiceBase {
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testByteNotNull() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Byte[] testByteObject
                byte[] testBytePrimitive
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testByteObject
		assertNotNull domainObject.testBytePrimitive
    }
/*
    void testByteMin() {
        def minSize = 10
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Byte[] testByteObject
                byte[] testBytePrimitive
                static constraints = {
                    testByteObject(min: $minSize)
                    testBytePrimitive(min: ${minSize}L)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testByteObject
		assertNotNull domainObject.testBytePrimitive
		assertTrue domainObject.testByteObject == minSize
		assertTrue domainObject.testBytePrimitive == minSize
    }

    void testByteMax() {
        def maxSize = 1000
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                Byte[] testByteObject
                byte[] testBytePrimitive
                static constraints = {
                    testByteObject(max: $maxSize)
                    testBytePrimitive(max: ${maxSize}L)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testByteObject
		assertNotNull domainObject.testBytePrimitive
		assertTrue domainObject.testByteObject == maxSize
		assertTrue domainObject.testBytePrimitive == maxSize
    }

*/

}
