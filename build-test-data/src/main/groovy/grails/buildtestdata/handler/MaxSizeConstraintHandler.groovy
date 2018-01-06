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
        Object propertyValue = getProperty(domain, propertyName)
        padMaxSize(domain, propertyName, propertyValue, maxSizeConstraint.maxSize)
    }

    static void padMaxSize(GormEntity domain, String propertyName, Object propertyValue, Integer maxSize) {
        //println "padMaxSize $propertyName $maxSize"
        if (propertyValue.respondsTo('size')) {
            //println "padMaxSize respondsToSize $propertyName $maxSize"
            Integer size = propertyValue.invokeMethod('size', null) as Integer
            if (size > maxSize) {
                Range range = (0..maxSize - 1)
                InvokerHelper.setProperty(domain, propertyName, propertyValue.invokeMethod('getAt', range))
            }
        }
    }
}
