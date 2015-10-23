package base

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test
import subclassing.RelatedClass
import subclassing.SubClass
import subclassing.SuperClass
import grails.buildtestdata.mixin.Build

@TestMixin(GrailsUnitTestMixin)
@Build([SubClass, RelatedClass, SuperClass])
class SubclassUnitTests {
    @Test
    void testSuccessfulBuildOfDomainSubclass() {
        def subClass = SubClass.build()
        assert subClass
        assert subClass.ident() > 0

        assert subClass.relatedClass.superClassInstances.contains(subClass)

        def relatedClass = RelatedClass.build()
        assert relatedClass
        assert relatedClass.ident() > 0

        def superClass = SuperClass.build()
        assert superClass
        assert superClass.ident() > 0
    }
}
