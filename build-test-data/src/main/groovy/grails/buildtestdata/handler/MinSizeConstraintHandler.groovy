package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.DomainInstanceRegistry
import grails.buildtestdata.DomainUtil
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.validation.constraints.MinSizeConstraint
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ToMany

@CompileStatic
class MinSizeConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        MinSizeConstraint minSizeConstraint = appliedConstraint as MinSizeConstraint
        Object propertyValue = getProperty(domain, propertyName)

        padMinSize(
            domain,
            constrainedProperty.appliedConstraints,
            propertyName,
            propertyValue,
            minSizeConstraint.minSize,
            circularCheckList
        )
    }

    static void padMinSize(GormEntity domain, Collection<Constraint> appliedConstraints, String propertyName, Object propertyValue, Integer minSize, CircularCheckList circularCheckList) {
        switch (propertyValue.getClass()) {
            case String:
                String stringValue = propertyValue as String

                // Try not to mangle email addresses and urls
                if (appliedConstraints?.find { it.name == ConstrainedProperty.URL_CONSTRAINT }) {
                    InvokerHelper.setProperty(
                        domain, propertyName, 'http://' + 'a'.padRight(minSize - 11, 'a') + '.com'
                    )
                }
                else if (appliedConstraints?.find { it.name == ConstrainedProperty.EMAIL_CONSTRAINT }) {
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
                    if (size < minSize) {
                        PersistentEntity entity = DomainUtil.getPersistentEntity(domain.class)
                        PersistentProperty property = entity.getPropertyByName(propertyName)

                        // Only ToMany properties will have an addTo method
                        if (property instanceof ToMany) {
                            ToMany toManyProp = property as ToMany
                            Class referencedClass = toManyProp?.associatedEntity?.javaClass
                            DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(referencedClass)

                            // Build new instances until we reach the minimum size required
                            ((size + 1)..minSize).each {
                                domain.addTo(propertyName, builder.buildWithoutSave([:], circularCheckList))
                            }
                        }
                    }
                }
        }
    }
}