package base

import abstractclass.AbstractSubClass
import abstractclass.AnotherConcreteSubClass
import abstractclass.ConcreteSubClass
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification

class AbstractDefaultUnitTests extends Specification implements UnitTestDataBuilder {
    void cleanup() {
        TestDataConfigurationHolder.reset()
    }

    void testBuildWithNoDefault() {
        mockDomain(AbstractSubClass)

        when:
        build(AbstractSubClass)

        then: "this is no longer supported in BTD 3.3 and later, specific default required"
        thrown(IllegalStateException)
    }

    void testSuccessfulBuildWithDefault() {
        TestDataConfigurationHolder.abstractDefault = ['abstractclass.AbstractSubClass': ConcreteSubClass]
        mockDomain(AbstractSubClass)

        when:
        def obj = build(AbstractSubClass)

        then:
        obj instanceof ConcreteSubClass
    }

    void testSuccessfulBuildWithDifferentDefault() {
        TestDataConfigurationHolder.abstractDefault = ['abstractclass.AbstractSubClass': AnotherConcreteSubClass]
        mockDomain(AbstractSubClass)

        when:
        def obj = build(AbstractSubClass)

        then:
        obj instanceof AnotherConcreteSubClass
    }
}
