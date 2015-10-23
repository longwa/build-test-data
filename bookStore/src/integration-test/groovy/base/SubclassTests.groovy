package base

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback
import subclassing.RelatedClass
import subclassing.SubClass
import subclassing.SuperClass

@Rollback
@TestMixin(IntegrationTestMixin)
class SubclassTests {
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
