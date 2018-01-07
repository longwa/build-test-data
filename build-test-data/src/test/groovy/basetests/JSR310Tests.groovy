package basetests

import spock.lang.Specification
import spock.lang.Unroll

class JSR310Tests extends Specification implements DomainTestBase {
    @Unroll
    void "create non-nullable property type #typeName"(String typeName) {
        when:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestJava$typeName {
                Long id
                Long version
                java.time.$typeName testProperty

                static constraints = {
                    testProperty(nullable: false)
                }
           }
        """)

        def domainObject = domainClass.build()

        then:
        domainObject != null
        domainObject.testProperty != null

        where:
        typeName << ['LocalDateTime', 'LocalDate', 'LocalTime']
    }
}
