package base

import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import subclassing.RelatedClass
import subclassing.SubClass
import subclassing.SuperClass

@Rollback
@Integration
class SubclassTests extends Specification implements TestDataBuilder {
    void testSuccessfulBuildOfDomainSubclass() {
        when:
        def subClass = build(SubClass)

        then:
        subClass
        subClass.ident() > 0
        subClass.relatedClass.superClassInstances.contains(subClass)

        when:
        def relatedClass = build(RelatedClass)

        then:
        relatedClass
        relatedClass.ident() > 0

        when:
        def superClass = build(SuperClass)

        then:
        superClass
        superClass.ident() > 0
    }
}
