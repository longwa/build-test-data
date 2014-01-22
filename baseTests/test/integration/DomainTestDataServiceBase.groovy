import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import grails.buildtestdata.DomainInstanceBuilder

@TestMixin(IntegrationTestMixin)
class DomainTestDataServiceBase {
    def grailsApplication
    def buildTestDataService

    def saveMock = { options = [:] ->
        delegate.id = 1
        return true
    }

    def validateMock = { ->
        return true
    }

    def identMock = { ->
        return delegate.id
    }

    def createDomainClass(String classText) {
        def domainClass = setUpDomainClass(classText)
        def domainArtefact = registerDomainClass(domainClass)
        buildTestDataService.decorateWithMethods(domainArtefact)
        return domainClass
    }

    def setUpDomainClass(String classText) {
        Class domainClass = new GroovyClassLoader().parseClass(classText)
        // mock save method as we don't have things fully hooked up to the DB for these test classes and we're doing validation based on constraints
        domainClass.metaClass.save = saveMock
        domainClass.metaClass.validate = validateMock        
        domainClass.metaClass.ident = identMock
        domainClass.metaClass.static.create = { ->
            domainClass.newInstance()
        }

        return domainClass
    }

    def registerDomainClass(Class domainClass) {
        grailsApplication.addArtefact(domainClass)
        return getDomainClassArtefactHandler().newArtefactClass(domainClass)
    }

    DomainClassArtefactHandler getDomainClassArtefactHandler() {
        grailsApplication.getArtefactHandlers().find { it.type == DomainClassArtefactHandler.TYPE }
    }


    DomainInstanceBuilder createDomainInstanceBuilder(String classText) {
        def domainArtefact = registerDomainClass( setUpDomainClass( classText ) )
        buildTestDataService.decorateWithMethods(domainArtefact)
        return buildTestDataService.getDomainInstanceBuilder(domainArtefact)
    }


}
