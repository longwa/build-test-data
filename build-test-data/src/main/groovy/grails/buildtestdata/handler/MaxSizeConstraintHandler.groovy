package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.validation.constraints.MaxSizeConstraint

@CompileStatic
class MaxSizeConstraintHandler extends AbstractConstraintHandler {
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
                InvokerHelper.setProperty(domain, propertyName, domain.invokeMethod('getAt', range))
            }
        }
    }
}
