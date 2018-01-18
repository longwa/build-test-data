package grails.buildtestdata

import groovy.transform.CompileStatic
import org.springframework.core.GenericTypeResolver

/**
 * Works like the Grails Testing Support's grails.testing.gorm.DomainUnitTest for testing a single entity
 */
@CompileStatic
trait UnitTestDomainBuilder<D> implements UnitTestDataBuilder {

    private D domainInstance
    static Class<D> domainClass

    /**
     * @return An instance of the domain class
     */
    D getDomain() {
        if (domainInstance == null) {
            //use buildWithoutSave to keep it consitent with how DomainUnitTest does it
            domainInstance = TestData.buildWithoutSave(getDomainUnderTest())
        }
        domainInstance
    }

    /** this is called by the {@link org.grails.testing.gorm.spock.DataTestSetupSpecInterceptor} */
    @Override
    Class<?>[] getDomainClassesToMock() {
        [getDomainUnderTest()].toArray(Class)
    }

    Class<D> getDomainUnderTest() {
        if (!domainClass)
            this.domainClass = (Class<D>) GenericTypeResolver.resolveTypeArgument(getClass(), UnitTestDomainBuilder.class)

        return domainClass
    }

    D build(Map<String, Object> propValues = [:]) {
        TestData.build(getDomainUnderTest(), propValues)
    }

    D buildWithoutSave(Map<String, Object> propValues = [:]) {
        TestData.buildWithoutSave(getDomainUnderTest(), propValues)
    }

    D buildLazy(Map<String, Object> propValues = [:]) {
        TestData.buildLazy(getDomainUnderTest(), propValues)
    }

}