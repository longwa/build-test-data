import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import org.junit.Test

@TestMixin(IntegrationTestMixin)
class DomainTestDataServiceByteTests extends DomainTestDataServiceBase {
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
