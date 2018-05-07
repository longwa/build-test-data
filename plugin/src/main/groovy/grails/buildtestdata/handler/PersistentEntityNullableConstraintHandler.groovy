package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Embedded
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.ToMany

@CompileStatic
class PersistentEntityNullableConstraintHandler extends NullableConstraintHandler {
    PersistentEntity persistentEntity
    MappingContext mappingContext

    PersistentEntityNullableConstraintHandler(PersistentEntity persistentEntity, MappingContext mappingContext) {
        this.persistentEntity = persistentEntity
        this.mappingContext = mappingContext
    }

    @Override
    void setValue(Object instance, String propertyName, Object value) {
        PersistentProperty domainProp = persistentEntity.getPropertyByName(propertyName)

        if (value && domainProp instanceof ManyToOne) {
            ManyToOne toOneProp = domainProp as ManyToOne
            GormEntity owningObject = (GormEntity) value
            owningObject.addTo(toOneProp.referencedPropertyName, instance)
        }
        else if (domainProp instanceof ToMany) {
            ToMany toManyProp = domainProp as ToMany
            ((GormEntity) instance).addTo(propertyName, value)
        }
        else {
            super.setValue(instance, propertyName, value)
        }
    }

    @Override
    Object determineNonStandardValue(Object instance, String propertyName, Constraint appliedConstraint,
                                     ConstrainedProperty constrainedProperty, DataBuilderContext ctx) {

        PersistentProperty domainProp = persistentEntity.getPropertyByName(propertyName)

        // Use the associatedEntity class and return a collection with the build object if its a ToMany
        if (domainProp instanceof ToMany) {
            ToMany toManyProp = domainProp as ToMany
            Class referencedClass = toManyProp?.associatedEntity?.javaClass
            def obj = ctx.satisfyNested(instance, propertyName, referencedClass)
            return [obj]
        }
        // Set save=true on this build so we don't get transient exception in integration tests, works fine in units
        // org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation
        else if (domainProp instanceof ManyToOne) {
            return ctx.satisfyNested(instance, propertyName, constrainedProperty.propertyType, true)
        }

        return ctx.satisfyNested(instance, propertyName, constrainedProperty.propertyType)
    }
}
