package base

import bookstore.Address
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class MinSizeTests extends Specification implements TestDataBuilder {
    void testEmailMinSize() {
        when:
        def domainObject = build(Address)

        then:
        domainObject
        domainObject.id
        domainObject.emailAddress
        domainObject.emailAddress.size() == 40
    }

    void testUrlMinSize() {
        when:
        def domainObject = build(Address)

        then:
        domainObject
        domainObject.id
        domainObject.webSite
        domainObject.webSite.size() == 40
    }
}
