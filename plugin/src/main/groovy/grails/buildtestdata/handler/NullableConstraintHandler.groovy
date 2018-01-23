package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.DomainInstanceRegistry
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.datastore.mapping.model.types.ToMany
import org.grails.datastore.mapping.reflect.ClassPropertyFetcher

import java.sql.Time
import java.sql.Timestamp

import static grails.buildtestdata.DomainUtil.getPersistentEntity
import static grails.buildtestdata.DomainUtil.propertyIsDomainClass
import static grails.buildtestdata.TestDataConfigurationHolder.getConfigPropertyNames
import static grails.buildtestdata.TestDataConfigurationHolder.getPropertyValues

@Slf4j
@CompileStatic
class NullableConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        Object value = determineBasicValue(propertyName, constrainedProperty)
        //println "handle $domain $propertyName"
        // If we can't find a basic value, see if this is a domain class in which case we will populate a new domain
        if (value == null) {
            if (propertyIsDomainClass(constrainedProperty.propertyType)) {
                populateDomainProperty(domain, propertyName, appliedConstraint, constrainedProperty, circularCheckList)
                return
            }

            // Last resort just try to instantiate a new empty value
            value = determineNonStandardValue(constrainedProperty)
        }

        if (domain.respondsTo(MetaProperty.getSetterName(propertyName))) {
            setProperty(domain, propertyName, value)
        }
        else {
            log.warn("Unable to set value for {}.{}, property is immutable", domain, propertyName)
        }
    }

    Object determineBasicValue(String propertyName, ConstrainedProperty constrainedProperty) {
        switch (constrainedProperty.propertyType) {
            case String:
                return propertyName
            case Byte:
                return new Byte('0')
            case Short:
            case Float:
            case Integer:
                return 0
            case Long:
                return 0L
            case Calendar:
                return new GregorianCalendar()
            case Currency:
                return Currency.getInstance(Locale.default)
            case TimeZone:
                return TimeZone.default
            case Locale:
                return Locale.default
            case java.sql.Date:
                return new java.sql.Date(new Date().time)
            case Time:
                return new Time(new Date().time)
            case Timestamp:
                return new Timestamp(new Date().time)
            case Date:
                return new Date()
            case BigDecimal:
                return new BigDecimal(0)
            case java.time.LocalDateTime:
                return java.time.LocalDateTime.now()
            case java.time.LocalDate:
                return java.time.LocalDate.now()
            case java.time.LocalTime:
                return java.time.LocalTime.now()
            case Boolean:
            case boolean:
                return false
            case { Class it -> it instanceof Number || it.isPrimitive() }:
                return 0
            case Byte[]:
            case byte[]:
                return [0xD, 0xE, 0xA, 0xD, 0xB, 0xE, 0xE, 0xF] as byte[]
            case Enum:
                Collection values = constrainedProperty.propertyType.invokeMethod("values", null) as Collection
                return values.first()
            default:
                log.warn("Unable to determine basic value type for {} with type {}", propertyName, constrainedProperty.propertyType)
                return null
        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    void populateDomainProperty(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        //println "populateDomainProperty $propertyName"
        PersistentEntity defDomain = getPersistentEntity(domain.getClass())
        PersistentProperty domainProp = defDomain.getPropertyByName(propertyName)
        ClassPropertyFetcher propertyFetcher = ClassPropertyFetcher.forClass(domain.getClass())

        // Ensure we don't loop on this domain
        circularCheckList.update(domain)

        if (domainProp instanceof OneToOne || propertyFetcher.getStaticPropertyValue('embedded', Collection)?.contains(propertyName)) {
            if (circularCheckList[constrainedProperty?.propertyType?.name]) {
                setProperty(domain, propertyName, circularCheckList[constrainedProperty?.propertyType?.name])
            }
            else {
                DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(domainProp.type)
                setProperty(domain, propertyName, builder.buildWithoutSave([:], circularCheckList))
            }
        }
        else if (domainProp instanceof ManyToOne) {
            ManyToOne toOneProp = domainProp as ManyToOne

            // Book has an Author, and Author isManyToOne to Book
            GormEntity owningObject
            if (circularCheckList[constrainedProperty?.propertyType?.name]) {
                owningObject = circularCheckList[constrainedProperty?.propertyType?.name]
            }
            else {
                DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(domainProp.type)
                owningObject = builder.build([:], circularCheckList)
            }

            owningObject.addTo(toOneProp.referencedPropertyName, domain)
        }
        else if (domainProp instanceof ToMany) {
            //println "domainProp instanceof ToMany $domainProp"
            ToMany toManyProp = domainProp as ToMany
            Class referencedClass = toManyProp?.associatedEntity?.javaClass

            // Author has many books, and author isOneToMany books
            // the build invocation below will set the books author on build, by getting it from the cirularChecklist
            def ownedObject
            if (circularCheckList[constrainedProperty?.propertyType?.name]) {
                ownedObject = circularCheckList[referencedClass?.name]
            }
            else {
                DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(referencedClass)
                ownedObject = builder.buildWithoutSave([:], circularCheckList)
            }

            domain.addTo(propertyName, ownedObject)
        }
        else {
            throw new RuntimeException("Can't cope with unset non-nullable property $propertyName of ${domain.class.name}")
        }
    }

    /**
     * This is probably something like JodaTime that can be configured to be save. We don't want to try to predict all the different mapping types
     * so we can't really handle user classes anyway.
     *
     * @param constrainedProperty
     * @return
     */
    Object determineNonStandardValue(ConstrainedProperty constrainedProperty) {
        Class propertyType = constrainedProperty.propertyType
        log.debug("Trying to instantiate an instance of {} from using the newInstance() method", propertyType)

        def configuredParams = getPropertyValues(propertyType.name, getConfigPropertyNames(propertyType.name))
        if (configuredParams) {
            log.debug "Instantiating with params: ${configuredParams}"
            return propertyType.newInstance(configuredParams)
        }
        else {
            return propertyType.newInstance()
        }
    }
}
