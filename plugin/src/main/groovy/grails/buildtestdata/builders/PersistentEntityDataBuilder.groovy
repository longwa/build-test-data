package grails.buildtestdata.builders

import grails.buildtestdata.DomainUtil
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
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne
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

        requiredDomainClasses = findRequiredDomainClasses()
    }

    Set<String> findPropsToSaveFirst() {
        if (getPersistentEntity().persistentProperties.any { it instanceof OneToOne && it.isOwningSide() }) {
            return requiredPropertyNames.findAll { String it ->
                PersistentProperty prop = getPersistentEntity().getPropertyByName(it)
                prop instanceof OneToOne && ((OneToOne) prop).isOwningSide()
            }
        }

        //findRequiredDomainPropertyNames()
    }

    Set<Class> findRequiredDomainClasses() {
        findRequiredAssociations().collect{ Association assc ->
            assc.associatedEntity.javaClass
        } as Set<Class>
    }

    List<Association> findRequiredAssociations() {
        persistentEntity.associations.findAll { Association assc ->
            requiredPropertyNames.contains(assc.name) && !(assc.associatedEntity instanceof EmbeddedPersistentEntity)
        }
    }

    @Override
    def build(DataBuilderContext ctx) {
        GormEntity instance = (GormEntity) buildWithoutSave(ctx)
        //instance.save(failOnError: true)
        save(instance, ctx)
        instance
    }

    @Override
    def buildWithoutSave(DataBuilderContext ctx) {
        def instance = super.doBuild(ctx)
        applyBiDirectionManyToOnes((GormEntity) instance)
        populateRequiredValues(instance, ctx)
        instance
    }

    /*
     * avoid transient exception for integration tests
     * if databinding occured with passed in data then this will assign both side of a bi-directional association
     * for example: If value is an Author and we're a Book, add us to the Author's set of books if there is one
     */
    void applyBiDirectionManyToOnes(GormEntity domainInstance) {
        PersistentEntity defDomain = DomainUtil.getPersistentEntity(domainInstance.class)
        for (Association association in defDomain.associations) {
            Object value = association.reader.read(domainInstance)
            if (association instanceof ManyToOne && value instanceof GormEntity) {
                ManyToOne manyToOneProp = association as ManyToOne
                GormEntity owningObject = value as GormEntity

                owningObject.addTo(manyToOneProp.referencedPropertyName, domainInstance)
            }
        }
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

    GormEntity save(GormEntity domainInstance, DataBuilderContext ctx) {
        if (ctx.knownInstances[domainInstance.class] || domainInstance instanceof Enum) {
            return domainInstance
        }
        Set<String> propsToSaveFirst = findPropsToSaveFirst()
        //avoid transient exception in integration tests by making sure we save in right order
        if (propsToSaveFirst) {
            log.debug("{} has these properties that we need to save first: {}", domainInstance.class.name, propsToSaveFirst)
            for (propertyName in propsToSaveFirst) {
                if (domainInstance?.hasProperty(propertyName)) {
                    GormEntity domProp = (GormEntity)domainInstance[propertyName]
                    ctx.knownInstances[domainInstance.class] = domainInstance
                    //domProp.save(failOnError: true)
                    save(domProp, ctx)
                }
            }
        }
        domainInstance.save(failOnError: true)

        domainInstance
    }

}
