package grails.buildtestdata.propsresolver

import groovy.transform.CompileStatic

@CompileStatic
class MapPropsResolver implements InitialPropertyResolver {
    Map<Class, Map<String, ?>> data

    MapPropsResolver(Map<Class, Map<String, ?>> data) {
        this.data = data
    }

    @Override
    Map<String, ?> getInitialProps(Class target, Object newInstance, Map<String, ?> provided) {
        data?.get(target)
    }

    @Override
    boolean supports(Class clazz) {
        data?.containsKey(clazz)
    }
}
