package grails.buildtestdata

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
class CircularCheckList {
    Map<String, GormEntity> checklist = [:]

    CircularCheckList update(GormEntity domain, boolean force = false) {
        if (force || !checklist.containsKey(domain.class.name)) {
            checklist[domain.class.name] = domain

            // Add superclasses as well
            Class clazz = domain.class.superclass
            while (clazz != Object) {
                if (DomainUtil.getPersistentEntity(clazz) != null) {
                    checklist[clazz.name] = domain
                }
                clazz = clazz.superclass
            }
        }
        this
    }

    boolean contains(GormEntity domain) {
        checklist.containsKey(domain.class.name)
    }

    GormEntity getAt(String name) {
        checklist.get(name)
    }

    void putAt(String name, Object domain) {
        checklist[name] = domain as GormEntity
    }
}
