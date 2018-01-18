package grails.buildtestdata.traits

import grails.buildtestdata.TestData
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
trait BuildTestDataEntity<D extends GormEntity<D>> {

    static D build(Map<String, Object> propValues = [:]) {
        TestData.build(this, propValues) as D
    }

    static D buildWithoutSave(Map<String, Object> propValues = [:]) {
        TestData.buildWithoutSave(this, propValues) as D
    }

    static D buildLazy(Map<String, Object> propValues = [:]) {
        TestData.buildLazy(this, propValues) as D
    }
}
