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
     * {@see #build(Map args, Class<T> entityClass, Map<String, Object> data)}
     */
    static <T> T build(Class<T> entityClass, Map<String, Object> data = [:]) {
        build([:], entityClass, data)
    }

    /**
     * builds the data using the passed in context
     * @param args a map of option
     *  - save : (default: true) whether to call the save method when its a GormEntity
     *  - find : (default: false) whether to try and find the entity in the datastore first
     *  - flush : (default: false) passed in the args to the GormEntity save method
     *  - failOnError : (default: true) passed in the args to the GormEntity save method
     *  - include : a list of the properties to build in addition to the required fields.
     *  - includeAll : (default: false) build tests data for all fields in the domain
     *  - data : a map of data to bind, can also just be in the args. usefull if you have a field named 'save' or 'find' etc..
     * @param entityClass the domain class for the entity that is built
     * @return the built entity.
     */
    static <T> T build(Map args, Class<T> entityClass) {
        Map newArgs = [:]
        Map<String, Object> propValues = [:]
        if (args){
            ['save', 'find', 'include', 'includeAll', 'flush', 'failOnError'].each { key ->
                if (args.containsKey(key)) newArgs[key] = args.remove(key)
            }
            propValues = ((args?.data) ? args.remove('data') : args) as Map<String, Object>
        }
        return build(newArgs, entityClass, propValues)
    }

    /**
     * builds and saves and instance of the domain entity
     *
     * @param args a map of option
     *  - save : (default: true) whether to call the save method when its a GormEntity
     *  - find : (default: false) whether to try and find the entity in the datastore first
     *  - flush : (default: false) passed in the args to the GormEntity save method
     *  - failOnError : (default: true) passed in the args to the GormEntity save method
     *  - include : a list of the properties to build in addition to the required fields.
     *  - includeAll : (default: false) build tests data for all fields in the domain
     * @param entityClass the domain class to use
     * @param data properties to set on the entity instance before it tries to build tests data
     * @return the built and saved entity instance
     */
    static <T> T build(Map args, Class<T> entityClass, Map<String, Object> data) {
        (T) findBuilder(entityClass).build(args, new DataBuilderContext(data))
    }

    /**
     * tried to find the an existing entity in the store, otherwise build its
     *
     * @param entityClass the domain class to use
     * @param data to be used in the finder or if not found used to build it.
     * @return the built and saved entity instance
     */
    static <T> T findOrBuild(Class<T> entityClass, Map<String, Object> data = [:]) {
        build([find: true], entityClass, data)
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
