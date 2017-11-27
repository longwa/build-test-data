package grails.buildtestdata

import grails.core.GrailsApplication
import grails.util.Holders
import groovy.transform.CompileStatic
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
}
