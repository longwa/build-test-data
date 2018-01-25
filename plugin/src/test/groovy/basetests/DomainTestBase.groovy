package basetests

import grails.buildtestdata.BuildDataTest

trait DomainTestBase extends BuildDataTest {

    Class createDomainClass(String classText) {
        Class domainClass = new GroovyClassLoader().parseClass(classText)
        mockDomain(domainClass)
        domainClass
    }
}
