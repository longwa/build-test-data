package basetests

import spock.lang.Specification

class ByteTests extends Specification implements DomainTestBase {

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
