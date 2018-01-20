package grails.buildtestdata

import grails.buildtestdata.builders.BuildTestDataApi
import grails.buildtestdata.builders.DataBuilderContext

/**
 * static helpers to build a domain instance with test data
 */
class TestData {

    /**
     * builds and saves and instance of the domain entity
     *
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built and saved entity instance
     */
    static <T> T build(Class<T> entityClass, Map<String, Object> data = [:]) {
        (T) BuildTestDataApi.findBuilder(entityClass).build(new DataBuilderContext(data))
    }

    /**
     * Uses the cached entity if it exists, otherwise build a new one
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built unsaved entity instance
     */
    static <T> T buildWithoutSave(Class<T> entityClass, Map<String, Object> data = [:]) {
        (T) BuildTestDataApi.findBuilder(entityClass).buildWithoutSave(new DataBuilderContext(data))
    }

    /**
     * Uses the cached entity if it exists, otherwise build a new one
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built and saved entity intance
     */
    static <T> T buildWithCache(Class<T> entityClass, Map<String, Object> data = [:]) {
        (T) BuildTestDataApi.findBuilder(entityClass).buildLazy(new DataBuilderContext(data))
    }

    static void loadTestDataConfig() {
        TestDataConfigurationHolder.loadTestDataConfig()
    }

    static void clearCache() {
        //DomainInstanceRegistry.clear()
    }
}
