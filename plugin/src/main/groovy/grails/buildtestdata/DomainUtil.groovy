package grails.buildtestdata

import groovy.transform.CompileStatic
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.reflect.ClassPropertyFetcher
import org.grails.datastore.mapping.reflect.ReflectionUtils

import java.lang.reflect.Modifier

@CompileStatic
class DomainUtil {
    static boolean propertyIsDomainClass(Class clazz) {
        propertyIsToOneDomainClass(clazz) || propertyIsToManyDomainClass(clazz)
    }

    static boolean propertyIsToOneDomainClass(Class clazz) {
        getPersistentEntity(clazz) != null
    }

    static PersistentEntity getPersistentEntity(Class clazz) {
        ClassPropertyFetcher.forClass(clazz).getStaticPropertyValue("gormPersistentEntity", PersistentEntity)
    }

    static boolean propertyIsToManyDomainClass(Class clazz) {
        Collection.isAssignableFrom(clazz)
    }

    static Class findConcreteSubclass(Class abstractClass) {
        if (isAbstract(abstractClass)) {
            TestDataConfigurationHolder.getAbstractDefaultFor(abstractClass.name)
        }
        else {
            return abstractClass
        }
    }

    static boolean isAbstract(Class clazz) {
        Modifier.isAbstract(clazz.getModifiers())
    }

    static Class[] expandSubclasses(Class<?>... classes) {
        classes.collectMany { Class clazz ->
            List<Class> result = [clazz]
            Class subClass = findConcreteSubclass(clazz)
            if (subClass != clazz) {
                result << subClass
            }
            result
        } as Class[]
    }
}
