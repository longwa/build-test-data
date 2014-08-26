package grails.buildtestdata.mixin

import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.test.mixin.support.SkipMethod
import grails.test.runtime.TestPluginRegistrar
import grails.test.runtime.TestPluginUsage

/**
 * This mixin replaces the use of the Grails @Mock annotation. The list of
 * classes given to @Build is used in a few ways:
 *
 * 1. The given classes are directly included for mocking
 * 2. Any non-nullable classes referenced by the initial class are found and included
 * 3. If any of the classes are abstract, a (random) concrete subclass is included for mocking
 * 4. If any of the classes have a parent class, the parent is included for mocking
 *
 * Since @Build necessarily includes additional classes in the graph (even if those classes are
 * not used by the test), it may perform worse than @Mock for complex object graphs.
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class BuildTestDataUnitTestMixin extends DomainClassUnitTestMixin implements TestPluginRegistrar {
    private static final Set<String> REQUIRED_FEATURES = (["domainClass", "buildTestData"] as Set<String>).asImmutable()

    BuildTestDataUnitTestMixin(Set<String> features) {
        super((REQUIRED_FEATURES + features) as Set<String>)
    }

    BuildTestDataUnitTestMixin() {
        super(REQUIRED_FEATURES)
    }

    @SkipMethod
    Iterable<TestPluginUsage> getTestPluginUsages() {
        TestPluginUsage.createForActivating(BuildTestDataTestPlugin)
    }

    /**
     * Programatically add classes to the mock list
     * @param classesToMock
     */
    void mockForBuild(List<Class> classesToMock) {
        MockDomainHelper helper = new MockDomainHelper(runtime)
        Collection<Class> allClasses = helper.resolveClasses(classesToMock as Set<Class>)
        helper.mockDomains(this, allClasses)
        helper.decorate(allClasses)
    }
}
