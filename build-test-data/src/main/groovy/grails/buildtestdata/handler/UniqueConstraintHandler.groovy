package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.MockErrors
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@SuppressWarnings("GroovyUnusedDeclaration")
@CompileStatic
class UniqueConstraintHandler implements ConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        // Unique isn't supported, if the value we've got in there isn't valid by this point, throw an error letting
        // the user know why we're not passing
        if (!constrainedProperty?.validate(domain, domain.metaClass.getProperty(domain, propertyName), new MockErrors(this))) {
            throw new ConstraintHandlerException("Unique constraint support not implemented: property $propertyName of ${domain.class.name}")
        }
    }
}
