package basetests

import grails.buildtestdata.BuildDataUnitTest

trait DomainTestBase extends BuildDataUnitTest {

    Class createDomainClass(String classText) {
        Class domainClass = new GroovyClassLoader().parseClass(classText)
        mockDomain(domainClass)
        domainClass
    }
}
