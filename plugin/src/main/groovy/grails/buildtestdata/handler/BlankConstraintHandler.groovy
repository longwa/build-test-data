package grails.buildtestdata.handler

import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic

@CompileStatic
class BlankConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty) {
        // shouldn't get here, as nullableHandler fires first and does not assign a blank value
        // though user could provide blank sample data
        setValue(instance,propertyName,'x')
    }
}
