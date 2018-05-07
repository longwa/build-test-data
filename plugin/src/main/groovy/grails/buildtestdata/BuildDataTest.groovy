package grails.buildtestdata

import grails.buildtestdata.mixin.Build
import grails.buildtestdata.testing.DependencyDataTest
import grails.buildtestdata.utils.MetaHelper
import grails.testing.spock.OnceBefore
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

/**
 * Unit tests should implement this trait to add build-test-data functionality.
 * Meant as a drop in replacement for Grails Testing Support's DataTest
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait BuildDataTest extends DependencyDataTest implements TestDataBuilder {

    @Override
    void mockDomains(Class<?>... domainClassesToMock) {
        super.mockDomains(domainClassesToMock)

        // Add build methods
        Class[] domainClasses = dataStore.mappingContext.getPersistentEntities()*.javaClass
        MetaHelper.addBuildMetaMethods(domainClasses)
    }

    @OnceBefore
    void handleBuildAnnotation() {
        Build build = this.getClass().getAnnotation(Build)
        if (build) {
            mockDomains(build.value())
        }
    }
}