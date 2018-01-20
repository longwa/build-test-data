package grails.buildtestdata.builders

import grails.buildtestdata.propsresolver.InitialPropsResolver
import org.springframework.core.annotation.AnnotationAwareOrderComparator

final class BuildTestDataApi {
    static InitialPropsResolver initialPropsResolver
    
    static final Map builders = new HashMap<Class, DataBuilder>()
    static final List<DataBuilderFactory> factories = []
    static{
        factories.add(new PersistentEntityDataBuilder.Factory())
        factories.add(new PogoTestDataBuilder.Factory())
        AnnotationAwareOrderComparator.sort(factories)
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
        for(factory in factories){
            if(factory.supports(clazz)){
                return factory.build(clazz)
            }
        }
    }

    static void clear() {
        builders.clear()
    }
}
