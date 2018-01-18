package grails.buildtestdata

class TestData {

    static <T> T build(Class<T> clazz, Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
        builder.build(propValues) as T
    }

    static <T> T buildWithoutSave(Class<T> clazz, Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
        builder.buildWithoutSave(propValues) as T
    }

    static <T> T buildLazy(Class<T> clazz, Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(clazz)
        (builder.findExisting(propValues) ?: builder.build(propValues)) as T
    }

    static void loadTestDataConfig() {
        TestDataConfigurationHolder.loadTestDataConfig()
    }

    static void clearCache() {
        DomainInstanceRegistry.clear()
    }
}
