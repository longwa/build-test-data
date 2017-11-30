import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.junit.Test

@Rollback
@Integration
class DomainTestDataServiceByteTests implements DomainTestDataServiceBase {
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
