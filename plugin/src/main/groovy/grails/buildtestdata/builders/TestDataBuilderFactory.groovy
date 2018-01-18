package grails.buildtestdata.builders

interface TestDataBuilderFactory<T extends DataBuilder> extends Comparable<TestDataBuilderFactory> {
    T build(Class target)
    boolean supports(Class clazz)
}
