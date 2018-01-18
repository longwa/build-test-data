package grails.buildtestdata.builders

import grails.buildtestdata.utils.Basics
import grails.databinding.DataBinder
import grails.databinding.SimpleDataBinder
import grails.databinding.SimpleMapDataBindingSource

class PogoTestDataBuilder implements DataBuilder{
    static class Factory extends AbstractTestDataBuilderFactory<PogoTestDataBuilder> {
        Factory(){
            super(Integer.MIN_VALUE)
        }
        @Override
        PogoTestDataBuilder build(Class target) {
            return new PogoTestDataBuilder(target)
        }
    }

    
    DataBinder dataBinder
    Class target

    PogoTestDataBuilder(Class target){
        this.target=target
        this.dataBinder = new SimpleDataBinder()
    }
    
    protected boolean isBasicType(Class type){
        Basics.isBasicType(type)
    }
    
    @Override
    def build(BuildTestDataContext ctx) {
        // Nothing to do, target exists already 
        if(ctx.target) return ctx.target
        
        Map initialProps = BuildTestDataApi.initialPropsResolver.getInitialProps(target)
        if(initialProps){
            if(ctx.data){
                ctx.data = [:] + initialProps + ctx.data 
            }
            else{
                ctx.data = [:] + initialProps
            }    
        }
        def instance = newInstance        
        
        if(ctx.data){
            dataBinder.bind(instance,new SimpleMapDataBindingSource(ctx.data))
        }
        instance
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
    def buildLazy(BuildTestDataContext ctx) {
        return build(ctx)
    }

    @Override
    def buildWithoutSave(BuildTestDataContext ctx){
        return build(ctx)
    }
}