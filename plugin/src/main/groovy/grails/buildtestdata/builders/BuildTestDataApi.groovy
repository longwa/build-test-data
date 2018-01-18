package grails.buildtestdata.builders

import grails.buildtestdata.propsresolver.InitialPropsResolver

final class BuildTestDataApi {
    static InitialPropsResolver initialPropsResolver
    
    static final Map builders = new HashMap<Class,grails.buildtestdata.TestDataBuilder>()
    static final List<TestDataBuilderFactory> factories = []
    static{
        factories.add(new GormEntityTestDataBuilder.Factory())
        factories.add(new PogoTestDataBuilder.Factory())
    }
    
    static void setInitialPropsResolver(InitialPropsResolver initialPropsResolver){
        this.initialPropsResolver = initialPropsResolver
    }
    static getInitialPropsResolver(){
        if(initialPropsResolver == null){
            initialPropsResolver = new InitialPropsResolver.EmptyInitialPropsResolver() 
        }
        initialPropsResolver
    }
    
    static DataBuilder findBuilder(Class clazz){
        if(!builders.containsKey(clazz)){
            builders.put(clazz,createBuilder(clazz))
        }
        builders.get(clazz)
    }

    static DataBuilder createBuilder(Class clazz){
        for(factory in factories.sort()){
            if(factory.supports(clazz)){
                return factory.build(clazz)
            }
        }
    }

    static void clear() {
        builders.clear()
    }
}
