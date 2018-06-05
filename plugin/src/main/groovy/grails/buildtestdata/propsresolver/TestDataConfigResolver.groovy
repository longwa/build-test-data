package grails.buildtestdata.propsresolver

import groovy.transform.CompileStatic
import static grails.buildtestdata.TestDataConfigurationHolder.*

@CompileStatic
class TestDataConfigResolver implements InitialPropertyResolver {
    @Override
    Map<String, ?> getInitialProps(Class target, Object newInstance, Map<String, ?> provided) {
        Set<String> missingProperties = getConfigPropertyNames(target.name) - provided.keySet()
        getPropertyValues(target.name, newInstance, missingProperties, provided as Map<String, Object>)
    }

    @Override
    boolean supports(Class clazz) {
        true
    }
}
