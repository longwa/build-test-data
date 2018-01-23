package grails.buildtestdata


import groovy.transform.CompileStatic
import org.junit.AfterClass
import org.junit.BeforeClass

/**
 * Integration tests, any class really, can implement this trait to add build-test-data functionality
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait TestDataBuilder {

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
        TestData.clear()
    }
}