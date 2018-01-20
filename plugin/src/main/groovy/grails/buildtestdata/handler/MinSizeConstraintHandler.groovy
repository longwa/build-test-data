package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper

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
        switch (constrained.propertyType) {
            case String:
                String stringValue = (propertyValue ?: '') as String

                // Try not to mangle email addresses and urls
                if (constrained.hasAppliedConstraint(ConstrainedProperty.URL_CONSTRAINT)) {
                    InvokerHelper.setProperty(
                        domain, propertyName, 'http://' + 'a'.padRight(minSize - 11, 'a') + '.com'
                    )
                }
                else if (constrained.hasAppliedConstraint(ConstrainedProperty.EMAIL_CONSTRAINT)) {
                    InvokerHelper.setProperty(
                        domain, propertyName, stringValue.padLeft(minSize, 'a')
                    )
                }
                else {
                    InvokerHelper.setProperty(
                        domain, propertyName, stringValue.padRight(minSize, '.')
                    )
                }
                break

            default:
                if (propertyValue.respondsTo('size')) {
                    Integer size = propertyValue.invokeMethod('size', null) as Integer
//                    if (size < minSize) {
//                        PersistentEntity entity = DomainUtil.getPersistentEntity(domain.class)
//                        PersistentProperty property = entity.getPropertyByName(propertyName)
//
//                        // Only ToMany properties will have an addTo method
//                        if (property instanceof ToMany) {
//                            ToMany toManyProp = property as ToMany
//                            Class referencedClass = toManyProp?.associatedEntity?.javaClass
//                            DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(referencedClass)
//
//                            // Build new instances until we reach the minimum size required
//                            ((size + 1)..minSize).each {
//                                domain.addTo(propertyName, builder.buildWithoutSave([:], circularCheckList))
//                            }
//                        }
//                    }
                }
        }
    }
}