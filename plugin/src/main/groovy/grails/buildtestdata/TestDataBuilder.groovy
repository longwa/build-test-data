package grails.buildtestdata

import groovy.transform.CompileStatic
import org.junit.AfterClass
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
        throw new UnsupportedOperationException("Not yet implemented")
    }

    @BeforeClass
    static void setupTestDataBuilder() {
        TestDataConfigurationHolder.loadTestDataConfig()
    }

    @AfterClass
    static void cleanupTestDataBuilder() {
        DomainInstanceRegistry.clear()
    }
}