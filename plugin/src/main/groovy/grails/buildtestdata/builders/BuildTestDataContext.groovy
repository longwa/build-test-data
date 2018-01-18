package grails.buildtestdata.builders

import org.codehaus.groovy.runtime.InvokerHelper

class BuildTestDataContext {
    Map<String,?> data    
    Object target
    
    Map<Class,Object> knownInstances = [:]
    
    BuildTestDataContext(Map<String,?> data){
        this.data=data
    }
    
    
    Object satisfyNested(Object instance, String property, Class propertyType){
        Class instanceType = instance.class
        if(propertyType.isAssignableFrom(instanceType)){
            return instance
        }
        def match = knownInstances.find {k,v-> propertyType.isAssignableFrom(k)}
        if(match){
            return match.value
        }        
        
        knownInstances.put(instanceType,instance)
        
        Object prevTarget = target
        Object prevData = data
        try{
            data = ((Map<String,?>)data[property])?:[:]
            target = InvokerHelper.getProperty(instance,property)
            
            BuildTestDataApi.findBuilder(propertyType).build(this)            
        }
        finally {
            data=prevData
            target=prevTarget
            knownInstances.remove(instanceType)
        }
    }
}
