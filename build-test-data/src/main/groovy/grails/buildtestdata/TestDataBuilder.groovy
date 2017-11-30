package grails.buildtestdata

import grails.buildtestdata.mixin.Build
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

/**
 * Integration tests should implement this trait to add build-test-data functionality
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait TestDataBuilder {
    public <T> T build(Class<T> clazz, Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
        builder.build(propValues) as T
    }

    public <T> T buildWithoutSave(Class<T> clazz, Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
        builder.buildWithoutSave(propValues) as T
    }

    public <T> T buildLazy(Class<T> clazz, Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
        (builder.findExisting(propValues) ?: builder.build(propValues)) as T
    }

    /**
     * Override this to override test data configuration for this test only
     */
    Closure doWithTestDataConfig() {
        null
    }

    @Before
    @CompileDynamic
    void handleBuildAnnotation() {
        Build build = this.getClass().getAnnotation(Build)
        if (build) {
            build.value().each { Class clazz ->
                clazz.metaClass.static.build = { Map<String, Object> props = [:] ->
                    DomainInstanceRegistry.lookup(clazz).build(props)
                }
            }
        }
    }

    @BeforeClass
    static void setupUnitTestDataBuilder() {
        TestDataConfigurationHolder.loadTestDataConfig()
    }

    @AfterClass
    static void cleanupUnitTestDataBuilder() {
        DomainInstanceRegistry.clear()
    }
}