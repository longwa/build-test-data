package grails.buildtestdata.propsresolver

interface InitialPropsResolver {
    Map<String,?> getInitialProps(Class target)
    
    static class EmptyInitialPropsResolver implements InitialPropsResolver{
        @Override
        Map<String, ?> getInitialProps(Class target) {
            return null
        }
    }
}