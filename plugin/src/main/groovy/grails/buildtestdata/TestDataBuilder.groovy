package grails.buildtestdata

import grails.testing.spock.OnceBefore
import groovy.transform.CompileStatic
import org.junit.AfterClass
import org.junit.Before

/**
 * Integration tests, any class really, can implement this trait to add build-test-data functionality
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait TestDataBuilder {
    private static boolean hasCustomTestDataConfig = false

    /** calls {@link TestData#build} */
    public <T> T build(Map args = [:], Class<T> clazz) {
        TestData.build(args, clazz)
    }

    /** calls {@link TestData#build} */
    public <T> T build(Class<T> clazz, Map<String, Object> propValues) {
        TestData.build([:], clazz, propValues)
    }

    /** calls {@link TestData#build} */
    public <T> T build(Map args, Class<T> clazz, Map<String, Object> propValues) {
        TestData.build(args, clazz, propValues)
    }

    /** calls {@link TestData#build} with [find: true] passed to args*/
    public <T> T findOrBuild(Class<T> clazz, Map<String, Object> propValues = [:]) {
        TestData.build([find: true], clazz, propValues)
    }

    /**
     * Override this to override test data configuration for this test class
     */
    Closure doWithTestDataConfig() {
        null
    }

    @Before
    void setupCustomTestDataConfig() {
        Closure testDataConfig = doWithTestDataConfig()
        if (testDataConfig) {
            TestDataConfigurationHolder.mergeConfig(testDataConfig)
            hasCustomTestDataConfig = true
        }
    }

    @AfterClass
    static void cleanupTestDataBuilder() {
        TestData.clear()
        if (hasCustomTestDataConfig) {
            TestDataConfigurationHolder.reset()
        }
    }
}