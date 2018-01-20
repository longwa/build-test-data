package grails.buildtestdata.builders

import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.utils.Basics
import grails.databinding.DataBinder
import grails.databinding.SimpleDataBinder
import grails.databinding.SimpleMapDataBindingSource
import org.springframework.core.annotation.Order

class PogoTestDataBuilder implements DataBuilder{

    @Order
    static class Factory implements DataBuilderFactory<PogoTestDataBuilder> {
        @Override
        PogoTestDataBuilder build(Class target) {
            return new PogoTestDataBuilder(target)
        }
        @Override
        boolean supports(Class clazz) {
            return true
        }
    }

    DataBinder dataBinder
    Class target


    PogoTestDataBuilder(){
        this.dataBinder = new SimpleDataBinder()
    }

    PogoTestDataBuilder(Class target){
        this.target=target
        this.dataBinder = new SimpleDataBinder()
    }
    
    protected boolean isBasicType(Class type){
        Basics.isBasicType(type)
    }
    
    @Override
    def build(DataBuilderContext ctx) {
        // Nothing to do, target exists already 
        if(ctx.target) return ctx.target

        //TODO fix to use new initialProps design
        //Map initialProps = BuildTestDataApi.initialPropsResolver.getInitialProps(target)
        Map initialProps = findMissingConfigValues(ctx.data)
        if(initialProps){
            if(ctx.data){
                ctx.data = [:] + initialProps + ctx.data 
            }
            else{
                ctx.data = [:] + initialProps
            }    
        }
        def instance = getNewInstance()

        if(ctx.data){
            dataBinder.bind(instance,new SimpleMapDataBindingSource(ctx.data))
        }
        instance
    }

    Map<String, Object> findMissingConfigValues(Map<String, Object> propValues) {
        Set<String> missingProperties = TestDataConfigurationHolder.getConfigPropertyNames(target.name) - propValues.keySet()
        TestDataConfigurationHolder.getPropertyValues(target.name, missingProperties, propValues)
    }


    def getNewInstance(){
        if(List.isAssignableFrom(target)){
            [] as List
        }
        else if(Set.isAssignableFrom(target)){
            [] as Set
        }
        else{
            target.newInstance()
        }
    }
    
    @Override
    def buildLazy(DataBuilderContext ctx) {
        return build(ctx)
    }

    @Override
    def buildWithoutSave(DataBuilderContext ctx){
        return build(ctx)
    }
}
