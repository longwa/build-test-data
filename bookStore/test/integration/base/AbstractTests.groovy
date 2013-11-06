package base

import abstractclass.AbstractClass
import abstractclass.AnotherConcreteSubClass
import abstractclass.ConcreteSubClass
import abstractclass.RelatedToAbstract
import abstractclass.AbstractSubClass

class AbstractTests extends GroovyTestCase {
    void testSuccessfulBuildOfDomainAbstractClass() {
        def abstractClass = AbstractClass.build()
        assertNotNull abstractClass
        assertTrue abstractClass.ident() > 0
        assertTrue abstractClass instanceof ConcreteSubClass

        def abstractSubClass = AbstractSubClass.build()
        assertNotNull abstractSubClass
        assertTrue abstractSubClass.ident() > 0
        assertTrue abstractSubClass instanceof AnotherConcreteSubClass

        def concreteClass = ConcreteSubClass.build()
        assertNotNull concreteClass
        assertTrue concreteClass.ident() > 0
    }

    void testSuccessfulBuildOfRelatedAbstractClass() {
        def related = RelatedToAbstract.build()
        assertNotNull related
        assertTrue related.ident() > 0
        assertNotNull related.genericParent
        assertTrue related.genericParent.ident() > 0
        assertTrue related.genericParent instanceof ConcreteSubClass
    }
}
