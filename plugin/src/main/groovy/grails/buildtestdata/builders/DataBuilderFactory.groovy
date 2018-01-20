package grails.buildtestdata.builders

interface DataBuilderFactory<T extends DataBuilder>{
    T build(Class target)
    boolean supports(Class clazz)
}
