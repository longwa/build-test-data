package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.MockErrors
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
class ValidatorConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        // Validate isn't supported, if the value we've got in there isn't valid by this point, throw an error letting
        // the user know why we're not passing
        if (!constrainedProperty?.validate(domain, getProperty(domain, propertyName), new MockErrors(this))) {
            throw new ConstraintHandlerException("Validator constraint not supported, attempted value (${getProperty(domain, propertyName)}) does not pass validation: property $propertyName of ${domain.class.name}")
        }
    }
}
