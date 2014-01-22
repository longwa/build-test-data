import org.junit.Test

class DomainTestDataServiceByteTests extends DomainTestDataServiceBase {
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testByteObject != null
		assert domainObject.testBytePrimitive != null
    }
}
