package grails.buildtestdata.handler

import grails.buildtestdata.MockErrors
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic

@CompileStatic
class ValidatorConstraintHandler extends AbstractHandler{

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty) {
        // validate isn't supported, if the value we've got in there isn't valid by this point, throw an error letting
        // the user know why we're not passing
        if ( !appliedConstraint?.validate(instance, getValue(instance,propertyName), new MockErrors(this)) ) {
            String error = "Validator constraint support not implemented in build-test-data, attempted value " +
                "(${getValue(instance,propertyName)}) does not pass validation: property $propertyName of ${instance.class.name}"
            throw new ConstraintHandlerException(error)
        }
    }
}

