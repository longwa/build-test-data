package basetests

import grails.buildtestdata.UnitTestDataBuilder

trait DomainTestDataServiceBase extends UnitTestDataBuilder {
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

    Class createDomainClass(String classText) {
        Class domainClass = setUpDomainClass(classText)
        mockDomain(domainClass)
        domainClass
    }

    Class setUpDomainClass(String classText) {
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
}
