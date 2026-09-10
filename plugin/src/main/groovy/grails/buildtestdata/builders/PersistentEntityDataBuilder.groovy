package grails.buildtestdata.builders

import grails.buildtestdata.handler.AssociationMinSizeHandler
import grails.buildtestdata.handler.PersistentEntityNullableConstraintHandler
import grails.buildtestdata.utils.DomainUtil
import grails.gorm.annotation.AutoTimestamp
import grails.gorm.api.GormAllOperations
import grails.gorm.validation.ConstrainedEntity
import grails.gorm.validation.ConstrainedProperty
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.config.Entity
import org.grails.datastore.mapping.model.EmbeddedPersistentEntity
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.config.GormProperties
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.datastore.mapping.reflect.ClassPropertyFetcher
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.util.ReflectionUtils
import org.springframework.validation.Validator

import java.lang.annotation.Annotation
import java.lang.reflect.Field

@Slf4j
@CompileStatic
class PersistentEntityDataBuilder extends ValidateableDataBuilder {

    @Order(Ordered.HIGHEST_PRECEDENCE)
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

    // Spring Data's audit annotations, which GORM 7.1+ also honours. Matched by simple name so there is no hard dependency.
    static final List<String> SPRING_DATA_TIMESTAMP_ANNOTATIONS = ['CreatedDate', 'LastModifiedDate'].asImmutable()

    Set<Class> requiredDomainClasses

    /** properties GORM stamps itself on insert/update, see {@link #findAutoTimestampPropertyNames()} */
    Set<String> autoTimestampPropertyNames

    PersistentEntityDataBuilder(Class target) {
        super(target)
        handlers.put(
            ConstrainedProperty.NULLABLE_CONSTRAINT,
            new PersistentEntityNullableConstraintHandler(getPersistentEntity(), getMappingContext())
        )
        handlers.put(
            ConstrainedProperty.MIN_SIZE_CONSTRAINT,
            new AssociationMinSizeHandler(getPersistentEntity())
        )

        requiredDomainClasses = findRequiredDomainClasses()
        autoTimestampPropertyNames = findAutoTimestampPropertyNames()
    }

    /**
     * Mirrors how GORM's AutoTimestampEventListener decides which properties it stamps: nothing when the entity is mapped
     * with autoTimestamp false, otherwise dateCreated, lastUpdated and anything annotated with @AutoTimestamp (or Spring
     * Data's @CreatedDate / @LastModifiedDate).
     */
    Set<String> findAutoTimestampPropertyNames() {
        PersistentEntity entity = persistentEntity
        Entity mappedForm = (Entity) entity.mapping?.mappedForm
        if (mappedForm != null && !mappedForm.autoTimestamp) {
            return [] as Set<String>
        }
        entity.persistentProperties.findAll { PersistentProperty property ->
            isAutoTimestampProperty(property)
        }*.name as Set<String>
    }

    boolean isAutoTimestampProperty(PersistentProperty property) {
        if (property.name == GormProperties.DATE_CREATED || property.name == GormProperties.LAST_UPDATED) {
            return true
        }
        Field field = ReflectionUtils.findField(persistentEntity.javaClass, property.name)
        if (field == null) {
            return false
        }
        field.isAnnotationPresent(AutoTimestamp) || field.annotations.any { Annotation annotation ->
            annotation.annotationType().simpleName in SPRING_DATA_TIMESTAMP_ANNOTATIONS
        }
    }

    Set<String> findPropsToSaveFirst() {
        if (getPersistentEntity().persistentProperties.any { it instanceof OneToOne && it.isOwningSide() }) {
            return requiredPropertyNames.findAll { String it ->
                PersistentProperty prop = getPersistentEntity().getPropertyByName(it)
                prop instanceof OneToOne && ((OneToOne) prop).isOwningSide()
            }
        }
        null
    }

    Set<Class> findRequiredDomainClasses() {
        findRequiredAssociations().collect { Association assc ->
            assc.associatedEntity.javaClass
        } as Set<Class>
    }

    List<Association> findRequiredAssociations() {
        persistentEntity.associations.findAll { Association assc ->
            requiredPropertyNames.contains(assc.name) && assc.associatedEntity && !(assc.associatedEntity instanceof EmbeddedPersistentEntity)
        }
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
        Validator validator = mappingContext.getEntityValidator(persistentEntity)
        if (validator instanceof ConstrainedEntity) {
            return ((ConstrainedEntity) validator).constrainedProperties
        }

        // This would mean the object isn't mocked with Grails, really shouldn't happen
        throw new RuntimeException("No constraints found for persistent entity ${targetClass.name}. Make sure it's mocked and properly initialized.")
    }

    MappingContext getMappingContext() {
        persistentEntity.getMappingContext()
    }

    PersistentEntity getPersistentEntity() {
        ClassPropertyFetcher.getStaticPropertyValue(targetClass, 'gormPersistentEntity', PersistentEntity)
    }

    /**
     * builds the data using the passed in context
     *
     * @param args optional argument map <br>
     *  - save        : (default: true) whether to call the save method when its a GormEntity <br>
     *  - find        : (default: false) whether to try and find the entity in the datastore first <br>
     *  - flush       : (default: false) passed in the args to the GormEntity save method <br>
     *  - failOnError : (default: true) passed in the args to the GormEntity save method <br>
     *  - include     : a list of the properties to build in addition to the required fields. <br>
     *  - includeAll  : (default: false) build tests data for all fields in the domain <br>
     * @param ctx the DataBuilderContext
     * @return the built entity.
     */
    @Override
    def build(Map args, DataBuilderContext ctx) {
        boolean saveFlag = args["save"] == null ? true : args.remove("save")
        boolean find = args["find"] == null ? false : args.remove("find")

        GormEntity instance = null
        //try looking it up in the store if "lazy" of find is set.
        if (find) {
            instance = findInStore(ctx)
        }
        //find wasn't set to true or it couldn't find anything if it was
        if (!instance) {
            instance = doBuild(ctx)
        }

        if (saveFlag) save(instance, ctx, args)
        instance
    }

    @Override
    GormEntity doBuild(DataBuilderContext ctx) {
        def instance = super.doBuild(ctx)
        applyBiDirectionManyToOnes((GormEntity) instance)
        populateRequiredValues(instance, ctx)
        populateAutoTimestamps(instance, ctx)
        (GormEntity) instance
    }

    /**
     * GORM stamps the auto-timestamp properties itself, but only when the insert actually happens. In the unit datastore,
     * or in Hibernate with a sequence generator, that is at flush time, so build() would otherwise hand back null
     * timestamps. GORM also never applies a nullable constraint to them, so populateRequiredValues has nothing to act on
     * and leaves them alone. Give them a value here; GORM replaces it with its own timestamp when the insert runs.
     */
    void populateAutoTimestamps(Object instance, DataBuilderContext ctx) {
        for (String propertyName in autoTimestampPropertyNames) {
            if (instance[propertyName] != null) {
                continue
            }
            ConstrainedProperty constrained = constraintsMap.get(propertyName)
            if (constrained) {
                log.debug("{}.{} is an auto-timestamp property GORM has not stamped yet, generating a value", targetClass.name, propertyName)
                handlers[ConstrainedProperty.NULLABLE_CONSTRAINT].handle(instance, propertyName, null, constrained, ctx)
            }
        }
    }

    @CompileDynamic
    GormEntity findInStore(DataBuilderContext ctx) {
        def ent
        if (ctx.data) {
            // If we are given the id specifically, fetch the object via get to ensure that we pull from the session
            // if it has already been inserted but not flushed.
            if (ctx.data.id) {
                ent = targetClass.get(ctx.data.id)
            }
            else {
                ent = targetClass.findWhere(ctx.data)
            }
        }
        else {
            List list = targetClass.list([max: 1])
            ent = list ? list.first() : null
        }
        return (GormEntity) ent
    }

    GormEntity save(GormEntity domainInstance, DataBuilderContext ctx, Map saveArgs = [:]) {
        if (ctx.knownInstances[domainInstance.class] || domainInstance instanceof Enum) {
            return domainInstance
        }
        Set<String> propsToSaveFirst = findPropsToSaveFirst()
        //avoid transient exception in integration tests by making sure we save in right order
        if (propsToSaveFirst) {
            log.debug("{} has these properties that we need to save first: {}", domainInstance.class.name, propsToSaveFirst)
            for (propertyName in propsToSaveFirst) {
                if (domainInstance?.hasProperty(propertyName)) {
                    GormEntity domProp = (GormEntity) domainInstance[propertyName]
                    ctx.knownInstances[domainInstance.class] = domainInstance
                    //do recursive call on this
                    save(domProp, ctx)
                }
            }
        }

        return entitySave(domainInstance, saveArgs)
    }

    /**
     * allows implementing tools to replace the closure for any special saving requirements
     */
    static Closure entitySave = { GormEntity domainInstance, Map saveArgs ->
        saveArgs['failOnError'] = saveArgs.containsKey('failOnError') ? saveArgs['failOnError'] : true
        return domainInstance.save(saveArgs)
    }

}
