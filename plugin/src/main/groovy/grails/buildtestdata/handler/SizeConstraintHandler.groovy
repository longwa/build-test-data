package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import grails.gorm.validation.DefaultConstrainedProperty
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.validation.constraints.SizeConstraint
import org.grails.validation.ConstrainedDelegate

@CompileStatic
class SizeConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        SizeConstraint sizeConstraint = appliedConstraint as SizeConstraint
        IntRange range = sizeConstraint.range

        MinSizeConstraintHandler.padMinSize(
            domain,
            getAppliedConstraints(constrainedProperty),
            propertyName,
            getProperty(domain, propertyName),
            range.from,
            circularCheckList
        )

        MaxSizeConstraintHandler.padMaxSize(domain, propertyName, getProperty(domain, propertyName), range.to)
    }

    @CompileDynamic static Collection<Constraint> getAppliedConstraints(ConstrainedProperty property) {
        if (property instanceof DefaultConstrainedProperty) {
            return property.appliedConstraints
        }
        if (property instanceof ConstrainedDelegate) {
            return property.appliedConstraints
        }
        throw new IllegalArgumentException("ConstrainedProperty of unknown type: ${property.getClass()}")
    }
}
