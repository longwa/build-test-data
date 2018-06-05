package grails.buildtestdata.propsresolver

import groovy.transform.CompileStatic

/**
 * Registry of property resolvers to be used
 */
@CompileStatic
class PropertyResolverRegistry {
    private static List<InitialPropertyResolver> resolvers = []

    /**
     * Register a property resolver. If multiple resolvers are registered that support
     * the same class, the last resolver registered will effectively have the highest priority
     * as any values returned from it will replace any earlier values
     *
     * @param resolver
     */
    static void registerResolver(InitialPropertyResolver resolver) {
        resolvers << resolver
    }

    static List<InitialPropertyResolver> lookupResolversFor(Class clazz) {
        resolvers.findAll { it.supports(clazz) }.reverse()
    }
}
