package grails.buildtestdata.testing

import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.utils.DomainUtil
import grails.buildtestdata.TestData
import grails.buildtestdata.builders.PersistentEntityDataBuilder
import grails.testing.gorm.DataTest
import groovy.transform.CompileStatic

/**
 * Overrides the mockDomains on DataTest to mock the dependency tree
 */
@CompileStatic
trait DependencyDataTest extends DataTest {
    /**
     * Empty, override in traits or the implemented tests.
     * Gets called/fired for all the mocked domain during mockDependencyGraph
     *
     * @param entityClasses
     */
    void onMockDomains(Class<?>... entityClasses) { }

    @Override
    void mockDomains(Class<?>... domainClassesToMock){
        mockDependencyGraph([] as Set, DomainUtil.expandSubclasses(domainClassesToMock))
    }

    void mockDependencyGraph(Set<Class> mockedList, Class<?>... domainClassesToMock) {
        // Resolve any additional build classes for this mock list
        domainClassesToMock = resolveAdditionalBuild(domainClassesToMock)

        // First mock these domains so they are registered with Grails
        DataTest.super.mockDomains(domainClassesToMock)

        // Call the next Trait in the chain
        onMockDomains(domainClassesToMock)

        mockedList.addAll(domainClassesToMock)

        Set<Class> requiredClasses = domainClassesToMock.collectMany { Class clazz ->
            // For domain instance building, we only care about concrete classes
            if (!DomainUtil.isAbstract(clazz)) {
                PersistentEntityDataBuilder builder = (PersistentEntityDataBuilder) TestData.findBuilder(clazz)
                //println "requiredDomainClasses ${builder.requiredDomainClasses}"
                builder.requiredDomainClasses
            }
            else {
                []
            }
        } as Set<Class>

        // Remove any that we've already seen
        requiredClasses.removeAll(mockedList)
        if (requiredClasses) {
            mockDependencyGraph(mockedList, requiredClasses as Class[])
        }
    }

    private Set<Class> resolveAdditionalBuild(Class<?>... domainClassesToMock) {
        Set<Class> allClasses = domainClassesToMock as Set<Class>
        allClasses.addAll(domainClassesToMock.collectMany { TestDataConfigurationHolder.getUnitAdditionalBuildFor(it.name) })
        allClasses
    }
}