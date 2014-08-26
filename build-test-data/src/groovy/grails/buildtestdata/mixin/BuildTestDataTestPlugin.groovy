package grails.buildtestdata.mixin

import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.runtime.TestEvent
import grails.test.runtime.TestPlugin
import grails.test.runtime.TestRuntime
import groovy.transform.CompileStatic

@CompileStatic
class BuildTestDataTestPlugin implements TestPlugin {
    String[] requiredFeatures = ['grailsApplication', 'coreBeans']
    String[] providedFeatures = ['buildTestData']
    int ordinal = 0

    @Override
    void onTestEvent(TestEvent event) {
        switch(event.name) {
            case 'beforeClass':
                beforeClass(event)
                break
            case 'before':
                before(event)
                break
        }
    }

    private void before(TestEvent event) {
        MockDomainHelper helper = new MockDomainHelper(event.runtime)
        helper.mockDomains(event.arguments.testInstance)
        helper.decorate()
    }

    private void beforeClass(TestEvent event) {
        TestDataConfigurationHolder.loadTestDataConfig()

        // Save the test class for later use
        Class testClass = event.arguments.testClass as Class
        Build buildAnnotation = testClass.getAnnotation(Build)
        Class[] buildClasses = buildAnnotation?.value()

        // No @Build annotation so not really much to do here
        if(!buildClasses) {
            return
        }

        MockDomainHelper helper = new MockDomainHelper(event.runtime)
        Collection<Class> allClasses = helper.resolveClasses(buildClasses as Set<Class>)
        helper.registerPersistentClasses(allClasses)

        // We need to recreate the runtime in order to properly register the beans during the
        // before callback. For some reason, you can't register domain beans in a beforeClass callback
        event.runtime.publishEvent("requestFreshRuntime")
    }

    @Override
    void close(TestRuntime runtime) {}
}
