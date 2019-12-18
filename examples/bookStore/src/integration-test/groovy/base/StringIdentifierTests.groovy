package base


import grails.buildtestdata.TestDataBuilder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import stringidentifiers.StringTypeRoot

@Integration
@Rollback
class StringIdentifierTests  extends Specification implements TestDataBuilder {

    void testBuildGraph() {
        when:
        def obj = build(StringTypeRoot)

        then:
        obj != null
        obj.id
        obj.simple.id
        obj.complex.id
        obj.complex.simple.id
    }

}
