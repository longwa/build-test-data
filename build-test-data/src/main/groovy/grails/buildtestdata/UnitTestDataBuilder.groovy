package grails.buildtestdata

import grails.testing.gorm.DataTest
import groovy.transform.CompileStatic
import org.junit.AfterClass
import org.junit.BeforeClass

/**
 * Unit tests should implement this trait to add build-test-data functionality
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait UnitTestDataBuilder extends DataTest implements TestDataBuilder {
    @Override
    void mockDomain(Class<?> domainClassToMock, List domains = []) {
        if (domains) {
            throw new IllegalArgumentException("mockDomain() with a list of domain objects to save is not supported (or useful in any way)")
        }

        resolveDependencyGraph(domainClassToMock)
    }

    @Override
    void mockDomains(Class<?>... domainClassesToMock) {
        resolveDependencyGraph(domainClassesToMock)
    }

    @BeforeClass
    static void setupUnitTestDataBuilder() {
        TestDataConfigurationHolder.loadTestDataConfig()
    }

    @AfterClass
    static void cleanupUnitTestDataBuilder() {
        DomainInstanceRegistry.clear()
    }

    private void resolveDependencyGraph(Class<?>... domainClassesToMock) {
        // First mock these domains so they are registered with Grails
        super.mockDomains(domainClassesToMock)

        // Now create instance builders
        for (Class clazz in domainClassesToMock) {
            DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
            Set<Class> requiredClasses = builder.requiredDomainClasses
            if (requiredClasses) {
                resolveDependencyGraph(requiredClasses as Class[])
            }
        }
    }
}