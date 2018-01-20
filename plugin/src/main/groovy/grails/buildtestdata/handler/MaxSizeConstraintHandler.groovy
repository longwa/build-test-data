package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic

@CompileStatic
class MaxSizeConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint,
                ConstrainedProperty constrainedProperty, DataBuilderContext ctx) {
        pad(instance, propertyName, constrainedProperty, ctx,constrainedProperty.getMaxSize())
    }

    void pad(Object instance, String propertyName, ConstrainedProperty constrainedProperty, DataBuilderContext ctx, int maxSize) {
        def value = getValue(instance, propertyName)
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

