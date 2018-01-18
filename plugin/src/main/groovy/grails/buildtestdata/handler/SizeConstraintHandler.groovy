package grails.buildtestdata.handler

import grails.buildtestdata.builders.BuildTestDataContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
class SizeConstraintHandler extends AbstractHandler {

    MinSizeConstraintHandler minSizeConstraintHandler = new MinSizeConstraintHandler()
    MaxSizeConstraintHandler maxSizeConstraintHandler = new MaxSizeConstraintHandler()

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint,
                ConstrainedProperty constrainedProperty, BuildTestDataContext ctx) {
        handle(instance,propertyName,constrainedProperty,ctx)
    }

    void handle(Object instance, String propertyName, ConstrainedProperty constrainedProperty, BuildTestDataContext ctx) {

        Object propertyValue = getValue(instance, propertyName)
        minSizeConstraintHandler.handle(instance, propertyName, constrainedProperty, ctx, constrainedProperty.minSize, propertyValue)

        maxSizeConstraintHandler.pad(
            instance, propertyName, constrainedProperty, ctx, constrainedProperty.size.max() as int
        )
    }
}
