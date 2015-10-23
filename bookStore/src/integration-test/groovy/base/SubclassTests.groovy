package base

import subclassing.RelatedClass
import subclassing.SubClass
import subclassing.SuperClass

class SubclassTests extends GroovyTestCase {
    void testSuccessfulBuildOfDomainSubclass() {
        def subClass = SubClass.build()
        assertNotNull subClass
        assertTrue subClass.ident() > 0

        assert subClass.relatedClass.superClassInstances.contains(subClass)

        def relatedClass = RelatedClass.build()
        assertNotNull relatedClass
        assertTrue relatedClass.ident() > 0

        def superClass = SuperClass.build()
        assertNotNull superClass
        assertTrue superClass.ident() > 0                     
    }
}
