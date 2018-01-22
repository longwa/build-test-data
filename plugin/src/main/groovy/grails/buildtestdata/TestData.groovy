package grails.buildtestdata


import grails.buildtestdata.builders.DataBuilder
import grails.buildtestdata.builders.DataBuilderContext
import grails.buildtestdata.builders.DataBuilderFactory
import grails.buildtestdata.builders.PersistentEntityDataBuilder
import grails.buildtestdata.builders.PogoDataBuilder
import grails.buildtestdata.propsresolver.InitialPropsResolver
import org.springframework.core.annotation.AnnotationAwareOrderComparator

/**
 * Primary static API to build a domain instance with test data
 */
class TestData {

    static InitialPropsResolver initialPropsResolver

    static final Map builders = new HashMap<Class, DataBuilder>()
    static final List<DataBuilderFactory> factories = []
    static{
        factories.add(new PersistentEntityDataBuilder.Factory())
        factories.add(new PogoDataBuilder.Factory())
        AnnotationAwareOrderComparator.sort(factories)
    }

    /**
     * builds and saves and instance of the domain entity
     *
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built and saved entity instance
     */
    static <T> T build(Class<T> entityClass, Map<String, Object> data = [:]) {
        (T) findBuilder(entityClass).build(new DataBuilderContext(data))
    }

    /**
     * Uses the cached entity if it exists, otherwise build a new one
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built unsaved entity instance
     */
    static <T> T buildWithoutSave(Class<T> entityClass, Map<String, Object> data = [:]) {
        (T) findBuilder(entityClass).buildWithoutSave(new DataBuilderContext(data))
    }

    /**
     * Uses the cached entity if it exists, otherwise build a new one
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built and saved entity intance
     */
    static <T> T buildWithCache(Class<T> entityClass, Map<String, Object> data = [:]) {
        (T) findBuilder(entityClass).buildLazy(new DataBuilderContext(data))
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
