package base

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Test
import subclassing.RelatedClass
import subclassing.SubClass
import subclassing.SuperClass

@Rollback
@Integration
class SubclassTests {
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
