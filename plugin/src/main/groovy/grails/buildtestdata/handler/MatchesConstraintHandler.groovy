package grails.buildtestdata.handler

import grails.buildtestdata.MockErrors
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import nl.flotsam.xeger.Xeger

@CompileStatic
class MatchesConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty) {
        // If what we have already matches, we are good
        if(!appliedConstraint.validate(instance,getValue(instance,propertyName),new MockErrors(this))){
            Xeger generator = new Xeger(constrainedProperty.matches)
            setValue(instance,propertyName,generator.generate())
        }
    }
}

