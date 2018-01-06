import basetests.TestDomain
import grails.buildtestdata.TestDataBuilder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Rollback
@Integration
class DomainTestDataServiceByteTests extends Specification implements TestDataBuilder {
    void testByteNotNull() {
        when:
		def domainObject = build(TestDomain)

        then:
		domainObject != null
		domainObject.testByteObject != null
		domainObject.testBytePrimitive != null
    }
}
