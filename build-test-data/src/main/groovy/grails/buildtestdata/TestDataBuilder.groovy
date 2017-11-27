package grails.buildtestdata

import grails.testing.gorm.DataTest
import groovy.transform.CompileStatic

/**
 * Use this trait instead of DataTest to provide automatic test data building
 *
 * @author Aaron Long
 * @since 3.3.0
 */
@CompileStatic
trait TestDataBuilder extends DataTest {
    Map<Class, DomainInstanceBuilder> _domainBuilderMap = [:]

    @Override
    void mockDomain(Class<?> domainClassToMock, List domains = []) {
        super.mockDomain(domainClassToMock, domains)
    }

    @Override
    void mockDomains(Class<?>... domainClassesToMock) {
        super.mockDomains(domainClassesToMock)
    }

    private void resolveDomainGraph(Class<?> ... domainClassesToMock) {
        super.mockDomains(domainClassesToMock)

        domainClassesToMock.colle
    }

    public <T> T build(Class<T> clazz, Map<String, Object> propValues = [:]) {
        clazz.newInstance()
    }

    public <T> T buildLazy(Class<T> clazz, Map<String, Object> propValues = [:]) {
        clazz.newInstance()
    }

    public <T> T buildWithoutSave(Class<T> clazz, Map<String, Object> propValues = [:]) {
        clazz.newInstance()
    }
}