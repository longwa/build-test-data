package grails.buildtestdata.testing

import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.DomainUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.testing.gorm.DataTest
import groovy.transform.CompileStatic
import org.grails.core.artefact.DomainClassArtefactHandler

@CompileStatic
trait BuildDataTest implements DataTest{
    @Override
    void mockDomains(Class<?>... domainClassesToMock) {
        DataTest.super.mockDomains(domainClassesToMock)
        
        TestDataConfigurationHolder.loadTestDataConfig()
        
        BuildTestDataService service = new BuildTestDataService()
        DomainUtil.grailsApplication=grailsApplication
        grailsApplication.getArtefacts(DomainClassArtefactHandler.TYPE).each {
            service.addBuildMethods(it)
        }
    }
}
