package base

import abstractclass.ConcreteSubClass
import abstractclass.RelatedToAbstract
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor

@TestFor(RelatedToAbstract)
@Build([RelatedToAbstract])
class AbstractRelatedUnitTests {
    void testSuccessfulBuild() {
        def theClass = RelatedToAbstract.build()
        assert theClass != null
        assert theClass.ident() > 0
        assert theClass.genericParent != null
        assert theClass.genericParent instanceof ConcreteSubClass
    }
}
