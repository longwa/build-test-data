package grails.buildtestdata

import groovy.transform.CompileStatic
import org.springframework.core.GenericTypeResolver

/**
 * Works like the Grails Testing Support's grails.testing.gorm.DomainUnitTest for testing a single entity
 */
@CompileStatic
trait UnitTestDomainBuilder<D> implements UnitTestDataBuilder {

    private D domainInstance
    private static Class<D> entityClass

    /**
     * @return An instance of the domain class
     */
    D getDomain() {
        if (domainInstance == null) {
            //use buildWithoutSave to keep it consitent with how DomainUnitTest does it
            this.domainInstance = TestData.buildWithoutSave(getEntityClass())
        }
        domainInstance
    }

    /**
     * same as getDomain()
     */
    D getEntity() {
        getDomain()
    }

    /** this is called by the {@link org.grails.testing.gorm.spock.DataTestSetupSpecInterceptor} */
    @Override
    Class<?>[] getDomainClassesToMock() {
        [getEntityClass()].toArray(Class)
    }

    Class<D> getEntityClass() {
        if (!entityClass)
            this.entityClass = (Class<D>) GenericTypeResolver.resolveTypeArgument(getClass(), UnitTestDomainBuilder.class)

        return entityClass
    }

    D build(Map<String, Object> propValues = [:]) {
        TestData.build(getEntityClass(), propValues)
    }

    D buildWithoutSave(Map<String, Object> propValues = [:]) {
        TestData.buildWithoutSave(getEntityClass(), propValues)
    }

}