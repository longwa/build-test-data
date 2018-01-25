package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic

@CompileStatic
class MinSizeConstraintHandler extends AbstractHandler{
   @Override
   void handle(Object instance, String propertyName, Constraint appliedConstraint,
               ConstrainedProperty constrainedProperty, DataBuilderContext ctx) {

       Object propertyValue = getValue(instance, propertyName)
       handle(instance, propertyName, constrainedProperty, ctx, constrainedProperty.minSize, propertyValue)
    }

    void handle(Object domain, String propertyName, ConstrainedProperty constrained, DataBuilderContext ctx,
                int minSize, Object propertyValue) {
        if(String.isAssignableFrom(String)){
            String stringValue = (propertyValue ?: '') as String
            // Try not to mangle email addresses and urls
            if (constrained.hasAppliedConstraint(ConstrainedProperty.URL_CONSTRAINT)) {
                setValue(domain, propertyName, "http://${'a'.padRight(minSize - 11, 'a')}.com")
            }
            else if (constrained.hasAppliedConstraint(ConstrainedProperty.EMAIL_CONSTRAINT)) {
                setValue(domain, propertyName, stringValue.padLeft(minSize, 'a'))
            }
            else {
                setValue(domain, propertyName, stringValue.padRight(minSize, '.'))
            }
        }
    }
}