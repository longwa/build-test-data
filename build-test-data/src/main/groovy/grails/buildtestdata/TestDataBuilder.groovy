package grails.buildtestdata

import groovy.transform.CompileStatic

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
}