package grails.buildtestdata

import grails.buildtestdata.builders.BuildTestDataApi
import groovy.transform.CompileStatic
import org.junit.AfterClass
import org.junit.BeforeClass

/**
 * Integration tests can implement this trait to add build-test-data functionality
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait TestDataBuilder {

    public <T> T build(Class<T> clazz, Map<String, Object> propValues = [:]) {
        TestData.build(clazz, propValues)
    }

    public <T> T buildWithoutSave(Class<T> clazz, Map<String, Object> propValues = [:]) {
        TestData.buildWithoutSave(clazz, propValues)
    }

    public <T> T buildLazy(Class<T> clazz, Map<String, Object> propValues = [:]) {
        TestData.buildWithCache(clazz, propValues)
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
        BuildTestDataApi.clear()
    }
}