package grails.buildtestdata.handler

import grails.buildtestdata.DomainUtil
import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
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
            // Book has an Author, and Author isManyToOne to Book
            GormEntity owningObject = (GormEntity) value
            owningObject.addTo(toOneProp.referencedPropertyName, instance)
            //super.setValue(instance, propertyName, value)
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

        //use the associatedEntity class and return a collection with the build object if its a ToMany
        if (domainProp instanceof ToMany) {
            ToMany toManyProp = domainProp as ToMany
            Class referencedClass = toManyProp?.associatedEntity?.javaClass
            def obj = ctx.satisfyNested(instance,propertyName,referencedClass)
            return [obj]
        }
        //else do normal
        ctx.satisfyNested(instance, propertyName, constrainedProperty.propertyType)
    }


//    void updateKnown(DataBuilderContext ctx, GormEntity gormEntity) {
//        if (!ctx.knownInstances.containsKey(gormEntity.class)) {
//            ctx.knownInstances[gormEntity.class] = gormEntity
//
//            // Add superclasses as well
//            Class clazz = gormEntity.class.superclass
//            while (clazz != Object) {
//                if (DomainUtil.getPersistentEntity(clazz) != null) {
//                    ctx.knownInstances[clazz] = gormEntity
//                }
//                clazz = clazz.superclass
//            }
//        }
//        this
//    }
}
