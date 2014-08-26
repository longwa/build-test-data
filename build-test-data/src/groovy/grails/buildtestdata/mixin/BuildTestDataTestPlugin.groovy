package grails.buildtestdata.mixin

import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.runtime.TestEvent
import grails.test.runtime.TestPlugin
import grails.test.runtime.TestRuntime
import groovy.transform.CompileStatic

@CompileStatic
class BuildTestDataTestPlugin implements TestPlugin {
    String[] requiredFeatures = ['domainClass', 'gorm']
    String[] providedFeatures = ['buildTestData']
    int ordinal = 0

    @Override
    void onTestEvent(TestEvent event) {
        switch(event.name) {
            case 'beforeClass':
                beforeClass(event)
                break
        }
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

        // Mock using the proper strategy
        MockDomainHelper helper = new MockDomainHelper(event.runtime, testClass)
        helper.mockForBuild(buildClasses as List<Class>)
    }

    @Override
    void close(TestRuntime runtime) {}
}
