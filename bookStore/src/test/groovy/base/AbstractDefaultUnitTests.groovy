package base

import abstractclass.AbstractClass
import abstractclass.AbstractSubClass
import abstractclass.AnotherConcreteSubClass
import abstractclass.ConcreteSubClass
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.BuildTestDataUnitTestMixin
import grails.test.mixin.TestMixin
import org.junit.Test

// Include the mixin directly so we can control the mocking in the test instead of up front
@TestMixin(BuildTestDataUnitTestMixin)
class AbstractDefaultUnitTests {
    void tearDown() {
        // we should reset the config holder when feeding it values in tests as it could cause issues
        // for other tests later on that are expecting the default config if we do not
        TestDataConfigurationHolder.reset()
    }

    @Test
    void testSuccessfulBuildNoDefault() {
        TestDataConfigurationHolder.abstractDefault = [:]
        mockForBuild([AbstractSubClass])

        // Chosen alphabetically since no default
        def obj = AbstractSubClass.build()
        assert obj instanceof AnotherConcreteSubClass
    }

    @Test
    void testSuccessfulBuildWithDefault() {
        TestDataConfigurationHolder.abstractDefault = ['abstractclass.AbstractSubClass': ConcreteSubClass]
        mockForBuild([AbstractSubClass])

        def obj = AbstractSubClass.build()
        assert obj instanceof ConcreteSubClass
    }

    @Test
    void testSuccessfulBuildWithDifferentDefault() {
        TestDataConfigurationHolder.abstractDefault = ['abstractclass.AbstractSubClass': AnotherConcreteSubClass]
        mockForBuild([AbstractSubClass])

        def obj = AbstractSubClass.build()
        assert obj instanceof AnotherConcreteSubClass
    }

    @Test
    void testSuccessfulBuildWithDefaultAsString() {
        TestDataConfigurationHolder.abstractDefault = ['abstractclass.AbstractSubClass': 'abstractclass.ConcreteSubClass']
        mockForBuild([AbstractSubClass])

        def obj = AbstractSubClass.build()
        assert obj instanceof ConcreteSubClass
    }

    // The default short circuits the search, so it must be concrete
    @Test
    void testFailureWithNonConcreteDefault() {
        TestDataConfigurationHolder.abstractDefault = ['abstractclass.AbstractSubClass': AbstractClass]
        mockForBuild([AbstractSubClass])
        shouldFail(InstantiationException) {
            AbstractSubClass.build()
        }
    }
}
