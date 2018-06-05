package grails.buildtestdata.propsresolver

import groovy.transform.CompileStatic

@CompileStatic
trait InitialPropertyResolver {
    /**
     * Retrun a set of initial properties for the given target class
     *
     * @param target the target class
     * @param newInstance the instance of the target being built
     * @param provided properties that have been resolved so far or are provided by the caller
     * @return a map of property names and values or null if no initial values
     */
    abstract Map<String, ?> getInitialProps(Class target, Object newInstance, Map<String, ?> provided)

    /**
     * Called after all properties are set but before the class is saved (or returned) to
     * the caller.
     * @param newInstance
     * @return likely the same instance, but could be a different instance if needed
     */
    def afterBuild(newInstance) {
        newInstance
    }

    /**
     * @return true if this resolver supports this class
     */
    abstract boolean supports(Class clazz)
}
