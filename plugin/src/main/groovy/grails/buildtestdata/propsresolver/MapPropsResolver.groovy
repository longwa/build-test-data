package grails.buildtestdata.propsresolver

class MapPropsResolver implements InitialPropsResolver{
    Map data
    MapPropsResolver(Map data){
        this.data=data
    }
    @Override
    Map<String, ?> getInitialProps(Class target) {
        return data.get(target)
    }
}
