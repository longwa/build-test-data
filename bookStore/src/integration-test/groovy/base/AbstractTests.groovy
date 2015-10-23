package base

import abstractclass.AbstractClass
import abstractclass.AnotherConcreteSubClass
import abstractclass.ConcreteSubClass
import abstractclass.RelatedToAbstract
import abstractclass.AbstractSubClass
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback
import org.junit.Test

@TestMixin(IntegrationTestMixin)
@Rollback
class AbstractTests {
    @Test
    void testSuccessfulBuildOfDomainAbstractClass() {
        def abstractClass = AbstractClass.build()
        assert abstractClass
        assert abstractClass.ident() > 0
        assert abstractClass instanceof ConcreteSubClass

        def abstractSubClass = AbstractSubClass.build()
        assert abstractSubClass
        assert abstractSubClass.ident() > 0

        // This could be either one of these
        assert abstractSubClass instanceof AnotherConcreteSubClass || abstractSubClass instanceof ConcreteSubClass

        def concreteClass = ConcreteSubClass.build()
        assert concreteClass
        assert concreteClass.ident() > 0
    }

    @Test
    void testSuccessfulBuildOfRelatedAbstractClass() {
        def related = RelatedToAbstract.build()
        assert related
        assert related.ident() > 0
        assert related.genericParent
        assert related.genericParent.ident() > 0
        assert related.genericParent instanceof ConcreteSubClass
    }
}
