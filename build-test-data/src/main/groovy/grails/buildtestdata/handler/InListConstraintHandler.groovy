package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.validation.constraints.InListConstraint

@CompileStatic
class InListConstraintHandler implements ConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        if (appliedConstraint instanceof InListConstraint) {
            List list = ((InListConstraint)appliedConstraint).list
            domain.metaClass.setProperty(domain, propertyName, list[0])
        }
    }
}
