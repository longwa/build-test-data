package grails.buildtestdata.handler

import grails.buildtestdata.builders.BuildTestDataContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic

@CompileStatic
interface ConstraintHandler {
    void handle(Object instance, String propertyName, Constraint appliedConstraint,
                ConstrainedProperty constrainedProperty, BuildTestDataContext ctx)
}
