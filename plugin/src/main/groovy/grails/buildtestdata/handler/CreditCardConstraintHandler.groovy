package grails.buildtestdata.handler

import groovy.transform.CompileStatic

@CompileStatic
class CreditCardConstraintHandler extends AbstractHandler{
    
    @Override
    void handle(Object instance, String propertyName) {
        setValue(instance,propertyName,'378282246310005')
    }
}
