package grails.buildtestdata

import grails.buildtestdata.mixin.Build
import grails.testing.gorm.DataTest
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.junit.Before

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

        resolveDependencyGraph([] as Set, expandSubclasses(domainClassToMock))
    }

    @Override
    void mockDomains(Class<?>... domainClassesToMock) {
        resolveDependencyGraph([] as Set, expandSubclasses(domainClassesToMock))
    }

    @Before
    @CompileDynamic
    void handleBuildAnnotation() {
        Build build = this.getClass().getAnnotation(Build)
        if (build) {
            resolveDependencyGraph([] as Set, expandSubclasses(build.value()))
        }
    }

    private Class[] expandSubclasses(Class<?>... classes) {
        classes.collectMany { Class clazz ->
            List<Class> result = [clazz]
            Class subClass = DomainUtil.findConcreteSubclass(clazz)
            if (subClass != clazz) {
                result << subClass
            }
            result
        } as Class[]
    }

    private void resolveDependencyGraph(Set<Class> mockedList, Class<?>... domainClassesToMock) {
        // First mock these domains so they are registered with Grails
        super.mockDomains(domainClassesToMock)
        mockedList.addAll(domainClassesToMock)

        Set<Class> requiredClasses = domainClassesToMock.collectMany { Class clazz ->
            // For domain instance building, we only care about concrete classes
            if (!DomainUtil.isAbstract(clazz)) {
                DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
                builder.requiredDomainClasses
            }
            else {
                []
            }
        } as Set<Class>

        // Remove any that we've already seen
        requiredClasses.removeAll(mockedList)
        if (requiredClasses) {
            resolveDependencyGraph(mockedList, requiredClasses as Class[])
        }
    }
}