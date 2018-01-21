package grails.buildtestdata.builders

import org.codehaus.groovy.runtime.InvokerHelper

class DataBuilderContext {
    Map<String,?> data    
    Object target
    
    Map<Class,Object> knownInstances = [:]

    DataBuilderContext(Map<String,?> data){
        this.data=data
    }

    Object satisfyNested(Object instance, String property, Class propertyType){
        //it the property is already set then just return it
        if(instance[property]) return instance[property]

        Class instanceType = instance.class
        if(propertyType.isAssignableFrom(instanceType)){
            return instance
        }
        def match = knownInstances.find {k,v-> propertyType.isAssignableFrom(k)}
        if(match){
//            if(propertyType.isAssignableFrom(match.value.class)) {
//                instance[property] = match.value
//            }
            return match.value
        }        
        
        knownInstances.put(instanceType,instance)
        Object prevTarget = target
        Object prevData = data
        Object returnInstance
        try{
            data = ((Map<String,?>)data[property])?:[:]
            target = instance[property]

            returnInstance = BuildTestDataApi.findBuilder(propertyType).buildWithoutSave(this)
//            if(propertyType.isAssignableFrom(returnInstance.class)) {
//                instance[property] = returnInstance
//            }
        }
        finally {
            data=prevData
            target=prevTarget
            knownInstances.remove(instanceType)
        }
        return returnInstance
    }
}
