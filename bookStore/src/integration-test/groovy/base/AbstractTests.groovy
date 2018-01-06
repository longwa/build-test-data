package base

import abstractclass.AbstractClass
import abstractclass.AnotherConcreteSubClass
import abstractclass.ConcreteSubClass
import abstractclass.RelatedToAbstract
import abstractclass.AbstractSubClass
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Integration
@Rollback
class AbstractTests extends Specification implements TestDataBuilder {

    void testSuccessfulBuildOfDomainAbstractClass() {
        when:
        def abstractClass = build(AbstractClass)
        then:
        abstractClass
        abstractClass.ident() > 0
        abstractClass instanceof ConcreteSubClass

        when:
        def abstractSubClass = build(AbstractSubClass)
        then:
        abstractSubClass
        abstractSubClass.ident() > 0

        // This could be either one of these
        abstractSubClass instanceof AnotherConcreteSubClass || abstractSubClass instanceof ConcreteSubClass

        when:
        def concreteClass = build(ConcreteSubClass)

        then:
        concreteClass
        concreteClass.ident() > 0
    }

    void testSuccessfulBuildOfRelatedAbstractClass() {
        when:
        def related = build(RelatedToAbstract)

        then:
        related
        related.ident() > 0
        related.genericParent
        related.genericParent.ident() > 0
        related.genericParent instanceof ConcreteSubClass
    }
}
