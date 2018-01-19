package grails.buildtestdata.builders

import grails.buildtestdata.DomainUtil
import grails.buildtestdata.handler.AssociationMinSizeHandler
import grails.buildtestdata.handler.PersistentEntityNullableConstraintHandler
import grails.buildtestdata.utils.ClassUtils
import grails.gorm.validation.Constrained
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.PersistentEntityValidator
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.OneToOne
import org.springframework.validation.Validator

@CompileStatic
class GormEntityTestDataBuilder extends ConstraintsTestDataBuilder{

    static class Factory extends AbstractTestDataBuilderFactory<GormEntityTestDataBuilder>{
        Factory(){
            super(100)
        }
        
        @Override
        GormEntityTestDataBuilder build(Class target) {
            return new GormEntityTestDataBuilder(target)
        }

        @Override
        boolean supports(Class clazz) {
            GormEntity.isAssignableFrom(clazz)
        }
    }

    // All constrained properties
    Map<String, ConstrainedProperty> constrainedProperties

    // All constrained associations
    Map<String, ConstrainedProperty> domainProperties

    Set<Class> requiredDomainClasses
    //Set<String> requiredPropertyNames
    Set<String> requiredDomainPropertyNames
    Set<String> propsToSaveFirst

    GormEntityTestDataBuilder(Class target) {
        super(target)        
        handlers.put(
            ConstrainedProperty.NULLABLE_CONSTRAINT, 
            new PersistentEntityNullableConstraintHandler(persistentEntity,mappingContext)
        )
        handlers.put(
            ConstrainedProperty.MIN_SIZE_CONSTRAINT,
            new AssociationMinSizeHandler(persistentEntity)
        )

        MappingContext mappingContext = persistentEntity.mappingContext
        PersistentEntityValidator entityValidator = mappingContext.getEntityValidator(persistentEntity) as PersistentEntityValidator
        constrainedProperties = (entityValidator?.constrainedProperties ?: [:]) as Map<String, ConstrainedProperty>

        requiredPropertyNames = findRequiredPropNames(constrainedProperties)
        domainProperties = findDomainProperties(constrainedProperties)
        requiredDomainPropertyNames = findRequiredDomainPropertyNames(domainProperties, requiredPropertyNames)
        requiredDomainClasses = findRequiredDomainClasses(domainProperties, requiredPropertyNames)

        propsToSaveFirst = findPropsToSaveFirst()
    }

    Set<String> findRequiredPropNames(Map<String, ConstrainedProperty> constrainedProperties) {
        Set<String> allPropertyNames = constrainedProperties.keySet()
        allPropertyNames.findAll { String propName ->
            !constrainedProperties[propName].isNullable()
        }
    }

    Set<Class> findRequiredDomainClasses(Map<String, ConstrainedProperty> domainProperties, Set<String> requiredPropertyNames) {
        domainProperties.findAll { Map.Entry<String, ConstrainedProperty> it ->
            requiredPropertyNames.contains(it.key) }
        .collect { Map.Entry<String, ConstrainedProperty> it ->
            it.value.propertyType
        } as Set<Class>
    }

    Map<String, ConstrainedProperty> findDomainProperties(Map<String, ConstrainedProperty> constrainedProperties) {
        constrainedProperties.findAll { Map.Entry<String, ConstrainedProperty> it ->
            DomainUtil.propertyIsToOneDomainClass(it.value.propertyType)
        }
    }

    Set<String> findRequiredDomainPropertyNames(Map<String, ConstrainedProperty> domainProperties, Set<String> requiredPropertyNames) {
        domainProperties.keySet().findAll { String it ->
            requiredPropertyNames.contains(it)
        }
    }

    Set<String> findPropsToSaveFirst() {
        if (persistentEntity.persistentProperties.any { it instanceof OneToOne && it.isOwningSide() }) {
            return requiredPropertyNames.findAll { String it ->
                PersistentProperty prop = persistentEntity.getPropertyByName(it)
                prop instanceof OneToOne && ((OneToOne) prop).isOwningSide()
            }
        }

        requiredDomainPropertyNames
    }

    @Override
    def build(BuildTestDataContext ctx) {
        GormEntity instance = (GormEntity) super.build(ctx)
        instance.save()
        instance
    }

    Set<String> findRequiredPropertyNames(Map<String, ConstrainedProperty> constrainedProperties) {
        Set<String> allPropertyNames = constrainedProperties.keySet()
        allPropertyNames.findAll { String propName ->
            !constrainedProperties[propName].isNullable()
        }
    }
//    @Override
//    Collection<String> findRequiredPropertyNames() {
//        Collection<String> requiredPropertyNames = super.findRequiredPropertyNames()
//        PersistentEntity persistentEntity = persistentEntity
//        Collection<String> requiredPersistentFields = (Collection<String>)persistentEntity.getPersistentProperties().inject([]){acc,item->
//            if(!item.nullable) acc.add(item.name)
//            return acc
//        }
//        return requiredPropertyNames + requiredPersistentFields
//    }

    @Override
    Map<String, ? extends Constrained> getConstraintsMap() {
        try{
            Validator validator = getValidator()
            
            if(validator instanceof PersistentEntityValidator){
                return ((PersistentEntityValidator)validator).constrainedProperties
            }
        }
        catch(Exception e){
            e.printStackTrace()
        }
        throw new RuntimeException("No constraints found for persistent entity ${target.name}. Make sure it's mocked and properly initialized.")
    }

    Validator getValidator(){
        mappingContext.getEntityValidator(persistentEntity)
    }
    
    MappingContext getMappingContext(){
        persistentEntity.getMappingContext()
    }
    
    PersistentEntity getPersistentEntity(){
        (PersistentEntity) ClassUtils.getStaticPropertyValue(target,'gormPersistentEntity')
    }

    @Override
    @CompileDynamic
    def buildLazy(BuildTestDataContext ctx) {
        (ctx.data? target.where(ctx.data):target.first()) ?:super.buildLazy(ctx)
    }

    @Override
    def buildWithoutSave(BuildTestDataContext ctx){
        return build(ctx)
    }
}
