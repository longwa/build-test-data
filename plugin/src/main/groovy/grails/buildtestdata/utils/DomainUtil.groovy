package grails.buildtestdata.utils

import grails.buildtestdata.TestDataConfigurationHolder
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.reflect.ClassPropertyFetcher

import java.lang.reflect.Modifier

@CompileStatic
class DomainUtil {
    static PersistentEntity getPersistentEntity(Class clazz) {
        ClassPropertyFetcher.getStaticPropertyValue(clazz, "gormPersistentEntity", PersistentEntity)
    }

    /**
     * check the test config for info on what conrete classes to sub in for abstracts
     */
    static Class findConcreteSubclass(Class abstractClass) {
        Class concreteClass = abstractClass
        if (isAbstract(abstractClass)) {
            concreteClass = TestDataConfigurationHolder.getAbstractDefaultFor(abstractClass.name)
            if (!concreteClass) {
                throw new IllegalArgumentException("No concrete subclass found for $abstractClass, use 'testDataConfig.abstractDefault' to configure")
            }
        }
        concreteClass
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
