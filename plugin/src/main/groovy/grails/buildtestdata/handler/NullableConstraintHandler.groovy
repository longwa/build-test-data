package grails.buildtestdata.handler

import grails.buildtestdata.builders.BuildTestDataContext
import grails.buildtestdata.utils.Basics
import grails.gorm.validation.Constrained
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class NullableConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint,
                ConstrainedProperty constrainedProperty, BuildTestDataContext ctx) {
        Object value = determineBasicValue(propertyName,constrainedProperty)
        if(value == null){
            value = determineNonStandardValue(instance,propertyName,appliedConstraint,constrainedProperty,ctx)
        }
        println "NullableConstraintHandler Setting val $propertyName $value"
        setValue(instance,propertyName,value)
    }

    Object determineNonStandardValue(Object instance, String propertyName, Constraint appliedConstraint,
                                     Constrained constrainedProperty, BuildTestDataContext ctx) {
        if(constrainedProperty instanceof ConstrainedProperty){
            ctx.satisfyNested(instance,propertyName,constrainedProperty.propertyType)
        }
    }

    Object determineBasicValue(String propertyName, ConstrainedProperty constrainedProperty) {
        switch (constrainedProperty.propertyType){
            case String: return propertyName
            default: return Basics.getBasicValue(constrainedProperty.propertyType)
        }
    }
//
//    void populateDomainProperty(domain, propertyName, appliedConstraint, constrainedProperty, circularCheckList){
//        GrailsDomainClass defDomain = getDomainArtefact(domain.class)
//        def domainProp = defDomain.propertyMap[propertyName]
//
//        circularCheckList.update(domain)
//        if (domainProp?.isOneToOne()
//            || (domain.metaClass.properties.name.contains('embedded')
//                && domain.embedded.contains(propertyName))) {
//            if (circularCheckList."${constrainedProperty?.propertyType.name}") {
//                domain."$propertyName" = circularCheckList."${constrainedProperty?.propertyType.name}"
//            } else {
//                domain."$propertyName" = domainProp.type.buildWithoutSave([:], circularCheckList)
//            }
//        } else if (domainProp?.isManyToOne()) {
//            // book has an author, and author isManyToOne to book
//            def owningObject
//            if (circularCheckList."${constrainedProperty?.propertyType.name}") {
//                owningObject = circularCheckList."${constrainedProperty?.propertyType.name}"
//            } else {
//                owningObject = domainProp.type.build([:], circularCheckList)
//            }
//            domain."$propertyName" = owningObject
//
//            addInstanceToOwningObjectCollection(owningObject, domain, domainProp)
//        } else if (domainProp?.isOneToMany() || domainProp?.isManyToMany()) {
//            // author has many books, and author isOneToMany books
//            // the build invocation below will set the books author on build, by getting it from the cirularChecklist
//            def ownedObject
//            if (circularCheckList."${constrainedProperty?.propertyType.name}") {
//                ownedObject = circularCheckList."${domainProp?.referencedPropertyType.name}"
//            } else {
//                ownedObject = domainProp?.referencedPropertyType.buildWithoutSave([:], circularCheckList)
//            }
//            addInstanceToOwningObjectCollection(domain, ownedObject, propertyName)
//        } else {
//            throw new Exception("can't cope with unset non-nullable property $propertyName of ${domain.class.name}")
//        }
//    }
//
//    def determineNonStandardValue(constrainedProperty) {
//        // probably something like JodaTime that can be configured to be saved
//        // we don't want to have to have all kinds of jar files in our code, plus we couldn't handle user created classes
//        Class propertyType = constrainedProperty.propertyType
//        log.debug("Trying to instantiate an instance of $propertyType from the class.newInstance() method")
//        def configuredParams = getPropertyValues(propertyType.name, getConfigPropertyNames(propertyType.name))
//        if (configuredParams) {
//            log.debug "Instantiating with params: ${configuredParams}"
//            return propertyType.newInstance(configuredParams)
//        } else {
//            return propertyType.newInstance()
//        }
//    }
//
//    static addInstanceToOwningObjectCollection(owningObject, domain, domainProp) {
//        def hasManyOfThisPropertyName = findHasManyPropertyName( domainProp.type, domain.class )
//
//        if (hasManyOfThisPropertyName) {
//            addInstanceToOwningObjectCollection(owningObject, domain, hasManyOfThisPropertyName)
//        } else {
//            log.warn "Unable to find hasMany property for $domain on ${domainProp.type}, ${domainProp.name} will only be populated on the belongsTo side"
//        }
//    }
//
//    static addInstanceToOwningObjectCollection(owningObject, domain, String hasManyOfThisPropertyName) {
//        // could have already added it depending on the direction we came from, don't add again
//        if (owningObject."$hasManyOfThisPropertyName"?.contains(domain)) return
//
//        owningObject."addTo${capitalize(hasManyOfThisPropertyName)}"(domain)
//    }

//    static findHasManyPropertyName(domain, Class hasManyOfClass) {
//        def hasManyPropertyName = domain.hasMany.find{ it.value == hasManyOfClass }?.key
//
//        if (!hasManyPropertyName && hasManyOfClass.superclass != Object ) {
//            // walk up the inheritance tree to see if one of our superclasses is
//            // actually in the hasMany instead of the current object
//            hasManyPropertyName = findHasManyPropertyName(domain, hasManyOfClass.superclass)
//        }
//
//        return hasManyPropertyName
//    }


}