package basetests

import grails.buildtestdata.UnitTestDataBuilder

trait DomainTestBase extends UnitTestDataBuilder {

    Class createDomainClass(String classText) {
        Class domainClass = new GroovyClassLoader().parseClass(classText)
        mockDomain(domainClass)
        domainClass
    }
}
