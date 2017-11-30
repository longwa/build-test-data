package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.MockErrors
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import nl.flotsam.xeger.Xeger
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.validation.constraints.MatchesConstraint

@CompileStatic
class MatchesConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        // If what we have already matches, we are good
        if (!constrainedProperty?.validate(domain, getProperty(domain, propertyName), new MockErrors(this))) {
            MatchesConstraint matchesConstraint = appliedConstraint as MatchesConstraint
            Xeger generator = new Xeger(matchesConstraint.regex)
            setProperty(domain, propertyName, generator.generate())
        }
    }
}
