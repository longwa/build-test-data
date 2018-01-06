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
        assert abstractClass
        assert abstractClass.ident() > 0
        assert abstractClass instanceof ConcreteSubClass

        when:
        def abstractSubClass = build(AbstractSubClass)
        then:
        assert abstractSubClass
        assert abstractSubClass.ident() > 0

        // This could be either one of these
        assert abstractSubClass instanceof AnotherConcreteSubClass || abstractSubClass instanceof ConcreteSubClass

        when:
        def concreteClass = build(ConcreteSubClass)

        then:
        assert concreteClass
        assert concreteClass.ident() > 0
    }

    void testSuccessfulBuildOfRelatedAbstractClass() {
        when:
        def related = build(RelatedToAbstract)

        then:
        assert related
        assert related.ident() > 0
        assert related.genericParent
        assert related.genericParent.ident() > 0
        assert related.genericParent instanceof ConcreteSubClass
    }
}
