package grails.buildtestdata.handler

import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import nl.flotsam.xeger.Xeger
import org.grails.datastore.gorm.validation.constraints.MatchesConstraint
import org.grails.datastore.mapping.validation.ValidationErrors

@CompileStatic
class MatchesConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty) {
        // If what we have already matches, we are good
        if(!appliedConstraint.validate(instance,getValue(instance,propertyName),new ValidationErrors(instance))){
            MatchesConstraint matchesConstraint = appliedConstraint as MatchesConstraint
            Xeger generator = new Xeger(matchesConstraint.regex)
            setValue(instance,propertyName,generator.generate())
        }
    }
}

