package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.buildtestdata.utils.Basics
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class NullableConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, DataBuilderContext ctx) {
        Object value = determineBasicValue(propertyName, constrainedProperty)
        if (value == null) {
            value = determineNonStandardValue(instance, propertyName, appliedConstraint, constrainedProperty, ctx)
        }

        setValue(instance, propertyName, value)
    }

    Object determineNonStandardValue(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, DataBuilderContext ctx) {
        ctx.satisfyNested(instance, propertyName, constrainedProperty.propertyType)
    }

    Object determineBasicValue(String propertyName, ConstrainedProperty constrainedProperty) {
        switch (constrainedProperty.propertyType) {
            case String: return propertyName
            default: return Basics.getBasicValue(constrainedProperty.propertyType)
        }
    }
}