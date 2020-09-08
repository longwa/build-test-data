package base

import abstractclass.ConcreteSubClass
import abstractclass.RelatedToAbstract
import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.TestDataConfigurationHolder
import spock.lang.Specification

class AbstractRelatedUnitTests extends Specification implements BuildDataUnitTest {
    void setup() {
        TestDataConfigurationHolder.reset()
    }

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
