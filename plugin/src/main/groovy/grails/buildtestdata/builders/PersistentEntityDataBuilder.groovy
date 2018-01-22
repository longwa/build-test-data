package grails.buildtestdata.builders

import grails.buildtestdata.handler.AssociationMinSizeHandler
import grails.buildtestdata.handler.PersistentEntityNullableConstraintHandler
import grails.buildtestdata.utils.ClassUtils
import grails.gorm.validation.ConstrainedEntity
import grails.gorm.validation.ConstrainedProperty
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.EmbeddedPersistentEntity
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Association
import org.springframework.core.annotation.Order
import org.springframework.validation.Validator

@Slf4j
@CompileStatic
class PersistentEntityDataBuilder extends ValidateableDataBuilder{

    @Order(100)
    static class Factory implements DataBuilderFactory<PersistentEntityDataBuilder> {
        @Override
        PersistentEntityDataBuilder build(Class target) {
            return new PersistentEntityDataBuilder(target)
        }

        @Override
        boolean supports(Class clazz) {
            GormEntity.isAssignableFrom(clazz)
        }
    }

    Set<Class> requiredDomainClasses
    //Set<String> propsToSaveFirst

    PersistentEntityDataBuilder(Class target) {
        super(target)        
        handlers.put(
            ConstrainedProperty.NULLABLE_CONSTRAINT, 
            new PersistentEntityNullableConstraintHandler(getPersistentEntity(),getMappingContext())
        )
        handlers.put(
            ConstrainedProperty.MIN_SIZE_CONSTRAINT,
            new AssociationMinSizeHandler(getPersistentEntity())
        )

        requiredDomainClasses = findRequiredDomainClasses(requiredPropertyNames)
    }

    Set<String> findRequiredPropNames(Map<String, ConstrainedProperty> constrainedProperties) {
        Set<String> allPropertyNames = constrainedProperties.keySet()
        return allPropertyNames.findAll { String propName ->
            !constrainedProperties[propName].isNullable()
        }
    }

    Set<Class> findRequiredDomainClasses(Set<String> requiredPropertyNames) {
        persistentEntity.associations.findAll { Association assc ->
            requiredPropertyNames.contains(assc.name) && !(assc.associatedEntity instanceof EmbeddedPersistentEntity)
        }.collect{ Association assc ->
            assc.associatedEntity.javaClass
        } as Set<Class>
    }

    @Override
    def build(DataBuilderContext ctx) {
        GormEntity instance = (GormEntity) buildWithoutSave(ctx)
        instance.save(failOnError: true, flush: true)
        instance
    }

    @Override
    def buildWithoutSave(DataBuilderContext ctx) {
        def instance = super.doBuild(ctx)
        //applyBiDirectionManyToOnes((GormEntity) instance)
        populateRequiredValues(instance, ctx)
        instance
    }

    @Override
    Map<String, ConstrainedProperty> getConstraintsMap() {
        try{
            Validator validator = mappingContext.getEntityValidator(persistentEntity)
            if(validator instanceof ConstrainedEntity){
                return ((ConstrainedEntity)validator).constrainedProperties
            }
        }
        catch(Exception e){
            e.printStackTrace()
        }
        throw new RuntimeException("No constraints found for persistent entity ${targetClass.name}. Make sure it's mocked and properly initialized.")
    }

    MappingContext getMappingContext(){
        persistentEntity.getMappingContext()
    }
    
    PersistentEntity getPersistentEntity(){
        (PersistentEntity) ClassUtils.getStaticPropertyValue(targetClass,'gormPersistentEntity')
    }

    @Override
    @CompileDynamic
    def buildLazy(DataBuilderContext ctx) {
        def ent
        if (ctx.data) {
            ent = targetClass.findWhere(ctx.data)
        } else {
            List list = targetClass.list([limit: 1])
            ent = list ? list.first() : null
        }
        if(!ent) ent = super.buildLazy(ctx)

        return ent
    }

}
