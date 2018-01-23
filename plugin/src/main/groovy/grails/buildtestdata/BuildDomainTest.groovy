package grails.buildtestdata

import groovy.transform.CompileStatic
import org.springframework.core.GenericTypeResolver

/**
 * Should works as a drop in replacement for the Grails Testing Support's
 * grails.testing.gorm.DomainUnitTest for testing a single entity using Generics
 * Its walks the tree so if you have a Book that has a required Author association you only need to do
 * implement BuildDomainTest<Book> and it will take care of mocking the Author for you.
 */
@CompileStatic
trait BuildDomainTest<D> implements BuildDataTest {

    private D entity
    private static Class<D> entityClass

    /**
     * keeps the method consistent with DomainUnitTest with the exception that this calls save(failOnError:true) by default
     * to change just pass in args. getDomain(save: false) for example will set it up and not call save at all.
     * getDomain(failOnError:false) would call save with throwing an exception on validation.
     * @return An instance of the domain class,
     */
    D getDomain(Map args = [:]) {
        getEntity(args)
    }

    /**
     * an entity instance of the Gorm class set as the generic. both getDomain and getEntity are the same thing
     */
    D getEntity(Map args = [:]) {
        if (entity == null) {
            this.entity = TestData.build(args, getEntityClass())
        }
        entity
    }

    /** this is called by the {@link org.grails.testing.gorm.spock.DataTestSetupSpecInterceptor} */
    @Override
    Class<?>[] getDomainClassesToMock() {
        [getEntityClass()].toArray(Class)
    }

    Class<D> getEntityClass() {
        if (!entityClass)
            this.entityClass = (Class<D>) GenericTypeResolver.resolveTypeArgument(getClass(), BuildDomainTest.class)

        return entityClass
    }

    D build() {
       build([:], getEntityClass(), [:])
    }

    D build(Map args) {
        build(args, getEntityClass())
    }

    D build(Map args, Map<String, Object> propValues) {
        build(args, getEntityClass(), propValues)
    }

}