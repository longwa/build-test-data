package grails.buildtestdata

import groovy.util.logging.Slf4j

import grails.buildtestdata.handler.ConstraintHandler
import grails.buildtestdata.handler.NullableConstraintHandler
import org.codehaus.groovy.runtime.InvokerHelper

import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import grails.gorm.validation.PersistentEntityValidator

import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne

import org.springframework.validation.Errors

@Slf4j
class DomainInstanceBuilder {
    Class javaClass
    PersistentEntity persistentEntity

    // All constrained properties
    Map<String, ConstrainedProperty> constrainedProperties

    // All constrained associations
    Map<String, ConstrainedProperty> domainProperties

    Set<Class> requiredDomainClasses
    Set<String> requiredPropertyNames
    Set<String> requiredDomainPropertyNames
    Set<String> propsToSaveFirst

    // Just for performance to avoid looking up the key types over and over
    Map<Class, Boolean> keyTypeCache = [:]

    DomainInstanceBuilder(Class<?> clazz) {
        javaClass = DomainUtil.findConcreteSubclass(clazz)
        if (!javaClass) {
            throw new IllegalStateException("Unable to load concrete class for ${clazz}, ensure the class exists and mockDomain(...) has been called with the configured subclass")
        }

        persistentEntity = DomainUtil.getPersistentEntity(javaClass)
        if (!persistentEntity) {
            throw new IllegalStateException("No persistent entity registered for ${javaClass.name}, check to make sure you have properly mocked for unit tests.")
        }

        MappingContext mappingContext = persistentEntity.mappingContext
        PersistentEntityValidator entityValidator = mappingContext.getEntityValidator(persistentEntity) as PersistentEntityValidator
        constrainedProperties = (entityValidator?.constrainedProperties ?: [:]) as Map<String, ConstrainedProperty>

        requiredPropertyNames = findRequiredPropertyNames(constrainedProperties)
        domainProperties = findDomainProperties(constrainedProperties)
        requiredDomainPropertyNames = findRequiredDomainPropertyNames(domainProperties, requiredPropertyNames)
        requiredDomainClasses = findRequiredDomainClasses(domainProperties, requiredPropertyNames)

        propsToSaveFirst = findPropsToSaveFirst()
    }

    private Set<String> findRequiredPropertyNames(Map<String, ConstrainedProperty> constrainedProperties) {
        Set<String> allPropertyNames = constrainedProperties.keySet()
        allPropertyNames.findAll { String propName ->
            !constrainedProperties[propName].isNullable()
        }
    }

    private Map<String, ConstrainedProperty> findDomainProperties(Map<String, ConstrainedProperty> constrainedProperties) {
        constrainedProperties.findAll { DomainUtil.propertyIsToOneDomainClass(it.value.propertyType) }
    }

    private Set<String> findRequiredDomainPropertyNames(Map<String, ConstrainedProperty> domainProperties, Set<String> requiredPropertyNames) {
        domainProperties.keySet().findAll { requiredPropertyNames.contains(it) }
    }

    private Set<Class> findRequiredDomainClasses(Map<String, ConstrainedProperty> domainProperties, Set<String> requiredPropertyNames) {
        domainProperties.findAll { requiredPropertyNames.contains(it.key) }.collect {
            it.value.propertyType
        } as Set<Class>
    }

    private Set<String> findPropsToSaveFirst() {
        if (persistentEntity.persistentProperties.any { it instanceof OneToOne && it.isOwningSide() }) {
            return requiredPropertyNames.findAll {
                PersistentProperty prop = persistentEntity.getPropertyByName(it)
                prop instanceof OneToOne && ((OneToOne) prop).isOwningSide()
            }
        }

        requiredDomainPropertyNames
    }

    GormEntity findExisting(Map<String, Object> propValues) {
        if (!propValues) {
            List<GormEntity> list = javaClass.invokeMethod("list", [limit: 1]) as List<GormEntity>
            list ? list.first() : null
        }
        else {
            javaClass.invokeMethod("findWhere", propValues) as GormEntity
        }
    }

    GormEntity buildWithoutSave(Map<String, Object> propValues, CircularCheckList circularCheckList = new CircularCheckList()) {
        GormEntity domainInstance = javaClass.invokeMethod("create", null) as GormEntity
        populateInstance(domainInstance, propValues, circularCheckList)
        circularCheckList.update(domainInstance, domainInstance.validate())
        domainInstance
    }

    GormEntity build(Map<String, Object> propValues, CircularCheckList circularCheckList = new CircularCheckList()) {
        GormEntity domainInstance = javaClass.invokeMethod("create", null) as GormEntity
        populateInstance(domainInstance, propValues, circularCheckList)

        // Save the domain
        domainInstance = save(domainInstance)

        // Prevent circular dependencies
        circularCheckList.update(domainInstance, domainInstance.validate())
        domainInstance
    }

    GormEntity populateInstance(GormEntity domainInstance, Map<String, Object> propValues, CircularCheckList circularCheckList) {
        propValues = findMissingConfigValues(propValues) + propValues

        for (property in propValues.keySet()) {
            setDomainPropertyValue(domainInstance, property, propValues[property])
        }

        Set<String> requiredMissingPropertyNames = (requiredPropertyNames - propValues.keySet()).findAll { propName ->
            !InvokerHelper.getProperty(domainInstance, propName)
        }

        log.debug("requiredMissingPropertyNames for {} = {}", javaClass.name, requiredMissingPropertyNames)

        for (propName in requiredMissingPropertyNames) {
            if (propName != 'dateCreated' && propName != 'lastUpdated') {
                createMissingProperty(domainInstance, propName, constrainedProperties[propName], circularCheckList)
            }
        }

        domainInstance
    }

    void setDomainPropertyValue(GormEntity domainInstance, String propertyName, Object value) {
        log.debug("Setting {}.{} to {}", javaClass.name, propertyName, value)

        if (propertyName.contains('.')) {
            setValueOnNestedProperty(domainInstance, propertyName, value)
        }
        else {
            InvokerHelper.setProperty(domainInstance, propertyName, value)
        }

        PersistentEntity defDomain = DomainUtil.getPersistentEntity(domainInstance.class)
        PersistentProperty domainProp = defDomain.getPropertyByName(propertyName)

        // If value is an Author and we're a Book, add us to the Author's set of books if there is one
        if (domainProp instanceof ManyToOne && value instanceof GormEntity) {
            ManyToOne manyToOneProp = domainProp as ManyToOne
            GormEntity owningObject = value as GormEntity

            owningObject.addTo(manyToOneProp.referencedPropertyName, domainInstance)
        }
    }

    void setValueOnNestedProperty(GormEntity domainInstance, String propertyName, Object value) {
        // This is an embedded property, i.e. 'bar.foo' = 23
        String[] props = propertyName.split(/\./)
        props[0..-2].inject(domainInstance) { current, next -> current[next] }[props[-1]] = value
    }

    Map<String, Object> findMissingConfigValues(Map<String, Object> propValues) {
        Set<String> missingProperties = TestDataConfigurationHolder.getConfigPropertyNames(javaClass.name) - propValues.keySet()
        TestDataConfigurationHolder.getPropertyValues(javaClass.name, missingProperties, propValues)
    }

    void createMissingProperty(GormEntity domainInstance, String propertyName, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        log.debug("Creating missing property domain {}, propname {}", domainInstance?.class?.name, propertyName)

        // First check if the default value satisfies the constraint
        // we could handle this like any other constraint except transient properties appear to be
        // non-nullable without actually having the nullable constraint
        new NullableConstraintHandler().handle(domainInstance, propertyName, null, constrainedProperty, circularCheckList)

        if (getErrors(constrainedProperty, domainInstance, propertyName).errorCount && !createProperty(domainInstance, propertyName, constrainedProperty, circularCheckList)) {
            log.warn "Failed to generate a valid value for {}.{}", domainInstance?.class?.name, propertyName
        }
        else {
            log.debug "Property name: {} - Created value: {}", propertyName, domainInstance?.metaClass?.getProperty(domainInstance, propertyName)
        }
    }

    Object createProperty(GormEntity domainInstance, String propertyName, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        log.debug("Building value for {}.{}", domainInstance?.class?.name, propertyName)

        sortedConstraints(constrainedProperty.appliedConstraints).find { Constraint appliedConstraint ->
            log.debug("{}.{} constraint, field before adjustment: {}", domainInstance?.class?.name, appliedConstraint?.name, domainInstance?.metaClass?.getProperty(domainInstance, propertyName))

            ConstraintHandler handler = ConstraintHandler.handlers[appliedConstraint.name]
            if (handler) {
                handler.handle(domainInstance, propertyName, appliedConstraint, constrainedProperty, circularCheckList)
                log.debug("${domainInstance?.class?.name}.$propertyName field after adjustment for ${appliedConstraint?.name}")
            }
            else {
                log.warn("Unable to find a constraint handler for {} with constraint: {}", domainInstance?.class?.name, appliedConstraint?.name)
            }

            if (!getErrors(constrainedProperty, domainInstance, propertyName).errorCount) {
                return true
            }
        }
    }

    Errors getErrors(ConstrainedProperty constrainedProperty, GormEntity domain, String propertyName) {
        Errors errors = new MockErrors(this)
        constrainedProperty.validate(domain, InvokerHelper.getProperty(domain, propertyName), errors)
        errors
    }

    GormEntity save(GormEntity domainInstance, CircularCheckList circularCheckList = new CircularCheckList()) {
        if (circularCheckList.contains(domainInstance) || domainInstance instanceof Enum) {
            return domainInstance
        }

        if (propsToSaveFirst) {
            log.debug("{} has these properties that we need to save first: {}", domainInstance.class.name, propsToSaveFirst)
            for (propertyName in propsToSaveFirst) {
                if (domainInstance.metaClass.hasProperty(propertyName)) {
                    save(InvokerHelper.getProperty(domainInstance, propertyName) as GormEntity, circularCheckList.update(domainInstance))
                }
            }
        }

        boolean hasAssignedKey = hasAssignedKey(domainInstance.getClass())
        if ((hasAssignedKey || domainInstance.ident() == null) && !domainInstance.save()) {
            throw new RuntimeException("Unable to build valid ${domainInstance.class.name} instance, errors: [${domainInstance.errors.collect { "\t$it\n" }}]")
        }

        domainInstance
    }

    Collection<Constraint> sortedConstraints(Collection<Constraint> appliedConstraints) {
        appliedConstraints.sort { Constraint a, Constraint b ->
            ConstraintHandler.CONSTRAINT_SORT_ORDER.indexOf(b.name) <=> ConstraintHandler.CONSTRAINT_SORT_ORDER.indexOf(a.name)
        }
    }

    /**
     * See if the given class (presumably a domain class) has an assigned key. We do this
     * by looking for and evaluating the static mapping block. This can come from the
     * GrailsDomainBinder, but that introduces a hibernate dependency.
     *
     * @param clazz
     * @return true if the class has a mapping block with an id(generator: '...') defined
     */
    private boolean hasAssignedKey(Class clazz) {
        boolean assigned = false

        // See if we've already check this instance
        if (keyTypeCache.containsKey(clazz)) {
            assigned = keyTypeCache[clazz]
        }
        else {
            if (clazz.metaClass.hasProperty(clazz, 'mapping')) {
                Object mappingProperty = InvokerHelper.getProperty(clazz, 'mapping')
                if (mappingProperty && mappingProperty instanceof Closure) {
                    MappingDelegate mappingDelegate = new MappingDelegate()

                    // Evaluate the mapping block
                    Closure block = mappingProperty.clone() as Closure
                    block.delegate = mappingDelegate
                    block.call()

                    assigned = mappingDelegate.isAssigned
                }
            }
        }

        // Sometimes the mapping block is in a parent class, we'll check those as well
        if (!assigned && clazz.superclass) {
            assigned = hasAssignedKey(clazz.superclass)
        }

        // Remember this class so we don't have to check again
        keyTypeCache[clazz] = assigned
        assigned
    }

    /**
     * Evaluate the mapping block to see if there is an id mapping with a generator defined
     * This is just a quick and dirty way to determine assigned keys without using hibernate
     */
    static class MappingDelegate {
        boolean isAssigned = false

        Object invokeMethod(String name, Object args) {
            if (name == "id" && args && args instanceof Object[]) {
                Object[] argArray = args as Object[]
                if (argArray[0] instanceof Map && argArray[0]['generator']) {
                    isAssigned = true
                }
            }
            null
        }
    }
}
