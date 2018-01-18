package grails.buildtestdata.builders

trait BuildTestData<T>{
    static T buildLazy(Map propValues=[:]){
        (T) BuildTestDataApi.findBuilder(this).buildLazy(new BuildTestDataContext(propValues))
    }
    static T build(Map propValues=[:]){
        (T) BuildTestDataApi.findBuilder(this).build(new BuildTestDataContext(propValues))
    }
}