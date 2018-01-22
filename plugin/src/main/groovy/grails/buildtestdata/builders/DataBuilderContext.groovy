package grails.buildtestdata.builders

import grails.buildtestdata.TestData
import groovy.transform.CompileStatic

@CompileStatic
class DataBuilderContext {
    Map<String,?> data    
    Object target
    
    Map<Class,Object> knownInstances = [:]

    DataBuilderContext(){
        this.data=[:]
    }

    DataBuilderContext(Map<String,?> data){
        this.data=data
    }

    Object satisfyNested(Object instance, String property, Class propertyType, boolean save = false){
        //it the property is already set then just return it
        if(instance[property]) return instance[property]

        if(propertyType.isAssignableFrom(instance.class)){
            return instance
        }

        //see if it exists in the knownInstances already and use it if so
        def match = knownInstances.find {k,v-> propertyType.isAssignableFrom(k)}
        if(match){
            return match.value
        }

        DataBuilderContext newCtx = createCopy(property)
        newCtx.target = instance[property]

        try{
            knownInstances.put(instance.class, instance)
            //the save is primarily here and will be true for ManyToOne so we don't get
            //org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation
            return TestData.findBuilder(propertyType).build(newCtx, save: save)
        }
        finally {
            knownInstances.remove(instance.class)
        }
    }

    Object satisfyNestedNew(Object instance, String property, Class propertyType){
        DataBuilderContext newCtx = createCopy(property)
        knownInstances.put(instance.class, instance)

        try{
            return TestData.findBuilder(propertyType).build(newCtx, save: false)
        }
        finally {
            knownInstances.remove(instance.class)
        }

    }

    DataBuilderContext createCopy(String property){
        def newCtx = new DataBuilderContext()
        newCtx.knownInstances = knownInstances
        if(data[property]) newCtx.data = (Map)data[property]
        return newCtx
    }
}
