package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.gorm.validation.ConstrainedProperty
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
            def value =  (0..minSize-1).collect{
                ctx.satisfyNested(instance,propertyName,((Association)property).associatedEntity.javaClass)
            }
            setValue(
                instance,
                propertyName,
                value
            )            
        }
        super.handle(instance, propertyName, constrained, ctx, minSize, propertyValue)
    }
}
