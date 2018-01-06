package grails.buildtestdata.traits

import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.DomainInstanceRegistry
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
trait BuildTestDataEntity<D extends GormEntity<D>> {


    static D build(Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(this)
        builder.build(propValues) as D
    }

    static D buildWithoutSave(Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(this)
        builder.buildWithoutSave(propValues) as D
    }

    static D buildLazy(Map<String, Object> propValues = [:]) {
        DomainInstanceBuilder builder = DomainInstanceRegistry.lookup(this)
        (builder.findExisting(propValues) ?: builder.build(propValues)) as D
    }

}
