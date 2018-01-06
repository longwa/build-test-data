package basetests

import org.junit.Test
import spock.lang.Specification

class DomainTestDataServiceByteTests extends Specification implements DomainTestDataServiceBase {

    void testByteNotNull() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestByteNotNullDomain {
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
