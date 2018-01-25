package grails.buildtestdata.builders

import grails.buildtestdata.utils.DomainUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.utils.Basics
import grails.databinding.DataBinder
import grails.databinding.SimpleDataBinder
import grails.databinding.SimpleMapDataBindingSource
import groovy.transform.CompileStatic
import org.springframework.core.annotation.Order

@CompileStatic
class PogoDataBuilder implements DataBuilder{

    @Order
    static class Factory implements DataBuilderFactory<PogoDataBuilder> {
        @Override
        PogoDataBuilder build(Class target) {
            return new PogoDataBuilder(target)
        }
        @Override
        boolean supports(Class clazz) {
            return true
        }
    }

    DataBinder dataBinder
    Class targetClass

    PogoDataBuilder(Class targetClass){
        //findConcreteSubclass takes care of subtituing in concrete classes for abstracts
        this.targetClass = DomainUtil.findConcreteSubclass(targetClass)
        this.dataBinder = new SimpleDataBinder()
    }

    @Override
    def build(DataBuilderContext ctx) {
        return build([:], ctx)
    }

    @Override
    def build(Map args, DataBuilderContext ctx) {
        return doBuild(ctx)
    }

    def doBuild(DataBuilderContext ctx) {
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

    Map<String, Object> findMissingConfigValues(Map propValues) {
        Set<String> missingProperties = TestDataConfigurationHolder.getConfigPropertyNames(targetClass.name) - propValues.keySet()
        TestDataConfigurationHolder.getPropertyValues(targetClass.name, missingProperties, propValues)
    }


    def getNewInstance(){
        if(List.isAssignableFrom(targetClass)){
            [] as List
        }
        else if(Set.isAssignableFrom(targetClass)){
            [] as Set
        }
        else{
            targetClass.newInstance()
        }
    }
}
