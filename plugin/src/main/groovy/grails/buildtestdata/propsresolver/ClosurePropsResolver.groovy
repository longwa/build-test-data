package grails.buildtestdata.propsresolver

class ClosurePropsResolver implements InitialPropertyResolver {
    Map<Class, Closure> initialProps

    ClosurePropsResolver(Map<Class, Closure> initialProps) {
        this.initialProps = initialProps
    }

    @Override
    Map<String, ?> getInitialProps(Class target, Object newInstance, Map<String, ?> provided) {
        Closure closure = initialProps.get(target)
        if (!closure) {
            return null
        }
        switch(closure.maximumNumberOfParameters) {
            case 1:
                return closure.call(provided) as Map<String, ?>
            case 2:
                return closure.call(provided, newInstance) as Map<String, ?>
            default:
                return closure.call() as Map<String, ?>
        }
    }

    @Override
    boolean supports(Class clazz) {
        initialProps.containsKey(clazz)
    }
}
