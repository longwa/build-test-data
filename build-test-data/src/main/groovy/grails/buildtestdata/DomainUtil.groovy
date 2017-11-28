package grails.buildtestdata

import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.util.Holders
import groovy.transform.CompileStatic
import org.grails.core.artefact.DomainClassArtefactHandler
import org.grails.datastore.mapping.model.PersistentEntity

@CompileStatic
class DomainUtil {
    static GrailsApplication grailsApplication = Holders.grailsApplication

    static boolean propertyIsDomainClass(Class clazz) {
        propertyIsToOneDomainClass(clazz) || propertyIsToManyDomainClass(clazz)
    }

    static boolean propertyIsToOneDomainClass(Class clazz) {
        getPersistentEntity(clazz) != null
    }

    static PersistentEntity getPersistentEntity(Class clazz) {
        grailsApplication.mappingContext.getPersistentEntity(clazz.name)
    }

    static boolean propertyIsToManyDomainClass(Class clazz) {
        Collection.isAssignableFrom(clazz)
    }

    static GrailsClass findConcreteSubclass(GrailsClass domainArtefact) {
        if (domainArtefact.isAbstract()) {
            // First see if we have a default defined for this domain class. If so,
            // we will use this. This is handy if you have alot of polymorphic associations to a
            // base class and want them to default to a certain type.
            Class abstractDefault = TestDataConfigurationHolder.getAbstractDefaultFor(domainArtefact.fullName)
            if (abstractDefault) {
                return grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, abstractDefault.name)
            }
            throw new UnsupportedOperationException("Unable to create concrete instance for ${domainArtefact.name}. Try adding an 'abstractDefault' section to TestDataConfig.")
        }
        else {
            return domainArtefact
        }
    }
}
