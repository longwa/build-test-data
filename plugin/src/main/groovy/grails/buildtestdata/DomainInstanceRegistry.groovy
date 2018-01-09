package grails.buildtestdata

import groovy.transform.CompileStatic

/**
 * Lookup for domain builder that we've already seen
 */
@CompileStatic
class DomainInstanceRegistry {
    private static Map<Class, DomainInstanceBuilder> registry = [:]

    /**
     * @return return a domain instance builder for the given class type
     */
    static DomainInstanceBuilder lookup(Class clazz) {
        if (!registry.containsKey(clazz)) {
            registry[clazz] = new DomainInstanceBuilder(clazz)
        }

        registry[clazz]
    }

    /**
     * Clear the registry (in cases where you want to construct new builders, such as when using guard)
     */
    static void clear() {
        registry.clear()
    }
}
