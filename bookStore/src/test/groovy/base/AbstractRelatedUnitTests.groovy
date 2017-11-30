package base

import abstractclass.ConcreteSubClass
import abstractclass.RelatedToAbstract
import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification

class AbstractRelatedUnitTests extends Specification implements UnitTestDataBuilder {
    void testSuccessfulBuild() {
        mockDomains(RelatedToAbstract, ConcreteSubClass)

        when:
        def theClass = build(RelatedToAbstract)

        then:
        assert theClass != null
        assert theClass.ident() > 0
        assert theClass.genericParent != null
        assert theClass.genericParent instanceof ConcreteSubClass
    }
}
