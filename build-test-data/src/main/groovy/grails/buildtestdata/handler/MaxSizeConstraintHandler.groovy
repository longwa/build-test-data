package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.validation.constraints.MaxSizeConstraint

@CompileStatic
class MaxSizeConstraintHandler implements ConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        MaxSizeConstraint maxSizeConstraint = appliedConstraint as MaxSizeConstraint
        padMaxSize(domain, propertyName, maxSizeConstraint.maxSize)
    }

    static void padMaxSize(GormEntity domain, String propertyName, Integer maxSize) {
        if (domain.respondsTo('size')) {
            Integer size = domain.invokeMethod('size', null) as Integer
            if (size > maxSize) {
                Range range = (0..maxSize - 1)
                domain.metaClass.setProperty(domain, propertyName, domain.invokeMethod('getAt', range))
            }
        }
    }
}
