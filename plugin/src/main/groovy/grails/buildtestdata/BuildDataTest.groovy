package grails.buildtestdata

import grails.buildtestdata.mixin.Build
import grails.buildtestdata.testing.DependencyDataTest
import grails.testing.spock.OnceBefore
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

/**
 * Unit tests should implement this trait to add build-test-data functionality
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait BuildDataTest extends DependencyDataTest implements TestDataBuilder {

    @Override
    void mockDomains(Class<?>... domainClassesToMock) {
        super.mockDomains(domainClassesToMock)
        //add build methods
        Class[] domainClasses = dataStore.mappingContext.getPersistentEntities()*.javaClass
        addBuildMetaMethods(domainClasses)
    }

    @CompileDynamic
    void addBuildMetaMethods(Class<?>... entityClasses){
        entityClasses.each { ec ->
            def mc = ec.metaClass
            mc.static.build = {
                return build(ec)
            }
            mc.static.build = { Map args ->
                return build(args, ec)
            }
            mc.static.build = { Map args, Map data ->
                return build(args, ec, data)
            }
            mc.static.findOrBuild = {
                return findOrBuild( ec, [:])
            }
            mc.static.findOrBuild = { Map data ->
                return findOrBuild( ec, data)
            }
        }
    }

    @OnceBefore
    void handleBuildAnnotation() {
        Build build = this.getClass().getAnnotation(Build)
        if (build) {
            mockDomains(build.value())
        }
    }

}