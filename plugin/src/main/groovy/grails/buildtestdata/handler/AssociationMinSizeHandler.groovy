package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association

class AssociationMinSizeHandler extends MinSizeConstraintHandler{
    PersistentEntity persistentEntity
    
    AssociationMinSizeHandler(PersistentEntity persistentEntity){
        this.persistentEntity=persistentEntity
    }

    @Override
    void handle(Object instance, String propertyName, ConstrainedProperty constrained, DataBuilderContext ctx,
                int minSize, Object propertyValue) {
        PersistentProperty property = persistentEntity.getPropertyByName(propertyName)
        if(property instanceof Association){
            Integer size = propertyValue.size()
            if (size < minSize) {
                ((size + 1)..minSize).each {
                    Class referencedClass = ((Association)property).associatedEntity.javaClass
                    def obj = ctx.satisfyNestedNew(instance, propertyName, referencedClass)
                    ((GormEntity) instance).addTo(propertyName, obj)
                }
            }

        } else {
            super.handle(instance, propertyName, constrained, ctx, minSize, propertyValue)
        }

    }
}
