package grails.buildtestdata.handler

import grails.buildtestdata.builders.BuildTestDataContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper

@CompileStatic
class MaxSizeConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint,
                ConstrainedProperty constrainedProperty, BuildTestDataContext ctx) {
        pad(instance, propertyName, constrainedProperty, ctx,constrainedProperty.getMaxSize())
    }

    void pad(Object instance, String propertyName, ConstrainedProperty constrainedProperty, BuildTestDataContext ctx, int maxSize) {
        def value = getValue(instance, propertyName)
        println "pad $propertyName $value"
        if (value instanceof Collection && value.size() > maxSize) {
            setValue(instance, propertyName, value.drop(value.size() - maxSize))
        } else if (value?.respondsTo('size')) {
            Integer size = value.invokeMethod('size', null) as Integer
            if (size > maxSize) {
                Range range = (0..maxSize - 1)
                setValue(instance, propertyName, value.invokeMethod('getAt', range))
            }
        }
    }
}

