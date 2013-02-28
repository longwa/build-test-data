package grails.buildtestdata.mixin

import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.junit.After
import org.springframework.beans.factory.BeanCreationException
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.beans.factory.config.BeanDefinition

class BuildTestDataUnitTestMixin extends DomainClassUnitTestMixin {

    static {
        TestDataConfigurationHolder.loadTestDataConfig()
    }

    private BuildTestDataService buildTestDataService
    private List processedClasses

    @After
    void cleanupBuildTestData() {
        buildTestDataService = null
        processedClasses = null
    }

    void mockForBuild(Class domainClassToMock) {
        ApplicationHolder.application = grailsApplication // DomainUtil needs this reference

        if (!buildTestDataService) {
            buildTestDataService = new BuildTestDataService()
            processedClasses = []
        }

        findDomainsToDecorate(domainClassToMock).each {
            buildTestDataService.decorateWithMethods it
        }
    }

    private List<GrailsDomainClass> findDomainsToDecorate(Class requiredDomain) {
        List domains = []

        if (!processedClasses.contains(requiredDomain)) {
            processedClasses << requiredDomain

            def grailsDomainClass = mockGrailsDomainClass(requiredDomain)

            // If this class is abstract, configure it's subclasses
            if( grailsDomainClass.isAbstract() ) {
                configureDomainSubclasses(grailsDomainClass)
            }

            domains << grailsDomainClass

            findRequiredDomainTypes(grailsDomainClass).each {
                domains.addAll findDomainsToDecorate(it)
            }
        }
        domains
    }

    /**
     * Mock the domain class and return the instance of the grails domain object
     * @param clazz
     * @return
     */
    private GrailsDomainClass mockGrailsDomainClass(Class clazz) {
        try {
            mockDomain clazz
        }
        catch(BeanCreationException ignored) {
            // If the domain object is abstract, we may not be able to create one. However, this will at least
            // register the bean so we can get the domain class
        }

        grailsApplication.getDomainClass(clazz.getName()) as GrailsDomainClass
    }

    private List<Class> findRequiredDomainTypes(GrailsDomainClass domainArtefact) {
        domainArtefact.constrainedProperties.values().findResults { ConstrainedProperty constrainedProperty ->
            GrailsDomainClassProperty property = domainArtefact.getPersistentProperty(constrainedProperty.propertyName)

            if (property?.association && !constrainedProperty.nullable) {
                Class type = property.referencedPropertyType ?: property.type
                if (type && DomainClassArtefactHandler.isDomainClass(type)) {
                    return type
                }
            }
            return null
        }
    }

    /**
     * Find subclasses and configure them on the parent object. We can't really use the built-in
     * Grails way of doing this since it relies on building a map of all the domain objects and their superclasses.
     *
     * @param domainClass
     * @return
     */
    private configureDomainSubclasses(GrailsDomainClass domainClass) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true)
        provider.addIncludeFilter(new AssignableTypeFilter(domainClass.getClazz()))

        // Scan all packages
        Set<BeanDefinition> components = provider.findCandidateComponents("")
        components.each { component ->
            Class cls = grailsApplication.classLoader.loadClass(component.beanClassName)
            GrailsDomainClass subclass = mockGrailsDomainClass(cls)
            domainClass.subClasses.add(subclass)
        }
    }
}
