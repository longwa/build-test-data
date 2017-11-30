package base

import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification
import subclassing.RelatedClass
import subclassing.SubClass
import subclassing.SuperClass

class SubclassUnitTests extends Specification implements UnitTestDataBuilder {
    void setupSpec() {
        mockDomains(SubClass, RelatedClass, SuperClass)
    }

    void testSuccessfulBuildOfDomainSubclass() {
        when:
        def subClass = build(SubClass)

        then:
        assert subClass
        assert subClass.ident() > 0

        assert subClass.relatedClass.superClassInstances.contains(subClass)

        when:
        def relatedClass = build(RelatedClass)

        then:
        assert relatedClass
        assert relatedClass.ident() > 0

        when:
        def superClass = build(SuperClass)

        then:
        assert superClass
        assert superClass.ident() > 0
    }
}
