package grails.buildtestdata

import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.validation.ConstrainedProperty
import groovy.transform.CompileStatic
import org.grails.core.artefact.DomainClassArtefactHandler

import java.lang.reflect.Modifier

/**
 * Helper class for resolving all of the required domain objects for a given initial set of
 * domain objects.
 *
 * @since 3.0.0
 */
@Deprecated
class MockDomainHelper {
    private GrailsApplication grailsApplication

    MockDomainHelper(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    /**
     * @param the initial set of classes (typically from the @Build annotation)
     * @return all classes that should be mocked for the given input classes
     */
    Collection<Class> resolveClasses(Set<Class> classes) {
        // Convert the list of classes given to GrailsDomainClass instances. We want to do this without
        // fully mocking so that we can get the full list of classes to mock and make a single
        // call to mockDomains() later with all the of the needed classes.
        List<GrailsDomainClass> initialDomainClassesToMock = classes.collect { getGrailsDomainClass(it) }

        // Resolve additional classes to mock
        findDomainClassesToMock(initialDomainClassesToMock)*.clazz
    }

    /**
     * Takes an initial list of domain classes and resolves the full list of domain classes
     * to be mocked. This includes required objects, subclasses, & parent classes.
     *
     * @param domainClassesToMock
     */
    private Collection<GrailsDomainClass> findDomainClassesToMock(List<GrailsDomainClass> domainClassesToMock) {
        addAdditionalBuildClasses(domainClassesToMock)

        // Recursively find all the associated domain classes which are non-nullable
        Map<String, GrailsDomainClass> requiredDomains = [:]
        domainClassesToMock.each {
            findRequiredDomainTypes(it, requiredDomains)
        }
        domainClassesToMock.addAll(requiredDomains.values())

        // Subclasses
        List<GrailsDomainClass> subClasses = []
        domainClassesToMock.each {
            if (it.isAbstract()) {
                subClasses.add(findDomainSubclasses(it))
            }
        }
        // Superclasses
        List<GrailsDomainClass> superClasses = []
        domainClassesToMock.each {
            superClasses.addAll(findDomainSuperclasses(it))
        }

        domainClassesToMock.addAll(subClasses)
        domainClassesToMock.addAll(superClasses)

        // One last pass to pick up other transitive dependenices
        addAdditionalBuildClasses(domainClassesToMock)

        // Make sure we don't have any duplicates, only want to mock once
        domainClassesToMock.unique { GrailsDomainClass gdc ->
            gdc.fullName
        }
    }

    /**
     * Get and/or register the given class. Mock registers the domain classes, but we need
     * to be able to work with them as domain classes prior to calling mock. Thus the need to
     * repeat this bit of the test code.
     *
     * @param clazz
     * @return
     */
    private GrailsDomainClass getGrailsDomainClass(Class clazz) {
        boolean isAbstract = Modifier.isAbstract(clazz.getModifiers())

        // If this isn't an abstract class,
        GrailsDomainClass domainClass = null
        if (!isAbstract) {
            domainClass = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, clazz.name) as GrailsDomainClass
        }
        if (!domainClass || isAbstract) {
            domainClass = registerGrailsDomainClass(clazz)
        }

        domainClass
    }

    /**
     * It would be nice to refactor the DomainClassUnitTestMixin to allow for better integration
     * with the new plugin infrastructure. That would allow us not to have to repeat code here.
     *
     * @param domainClassToMock
     * @return
     */
    private GrailsDomainClass registerGrailsDomainClass(Class<?> domainClassToMock) {
        (GrailsDomainClass) grailsApplication.addArtefact(DomainClassArtefactHandler.TYPE, domainClassToMock)
    }

    /**
     * Recursively find all of the required domain classes for domain artefact and add to the _requiredDomains_ list.
     *
     * @param domainArtefact
     * @param requiredDomains running list of domain classes required so far
     */
    private void findRequiredDomainTypes(GrailsDomainClass domainArtefact, Map<String, GrailsDomainClass> requiredDomains) {
        List<GrailsDomainClass> domains = []

        // Look at each constrained property
        List<ConstrainedProperty> constrainedProperties = domainArtefact.constrainedProperties.values() as List<ConstrainedProperty>
        constrainedProperties.each { ConstrainedProperty constrainedProperty ->
            GrailsDomainClassProperty property = domainArtefact.getPersistentProperty(constrainedProperty.propertyName)
            if (property?.association && !constrainedProperty.nullable) {
                Class type = property.referencedPropertyType ?: property.type

                // If this is a domain class and it's not already been registered, register it and add to the list
                if (type && DomainClassArtefactHandler.isDomainClass(type)) {
                    domains << getGrailsDomainClass(type)
                }
            }
        }

        // Recursively find all required. Make sure we only look at each domain once
        domains.each {
            if (!requiredDomains.containsKey(it.fullName)) {
                requiredDomains[it.fullName] = it
                findRequiredDomainTypes(it, requiredDomains)
            }
        }
    }

    /**
     * Find subclasses and return them for mocking. We can't really use the built-in
     * Grails way of doing this since it relies on building a map of all the domain objects and their superclasses.
     *
     * @param domainClass
     * @return list of subclasses for this parent
     */
    private GrailsClass findDomainSubclasses(GrailsDomainClass domainClass) {
        DomainUtil.findConcreteSubclass(domainClass)
    }

    /**
     * Find all domain superclasses for the given domain class
     *
     * @param domainClass
     * @return list of superclasses
     */
    private List<GrailsDomainClass> findDomainSuperclasses(GrailsDomainClass domainClass) {
        List<GrailsDomainClass> list = []

        Class superClass = domainClass.clazz.superclass
        while (superClass) {
            if (DomainClassArtefactHandler.isDomainClass(superClass)) {
                list << getGrailsDomainClass(superClass)
                superClass = superClass.superclass
            }
            else {
                superClass = null
            }
        }

        list
    }

    /**
     * Take the given list of domain classes and add an additional classes specified
     * in the unitAdditionalBuild configuration.
     *
     * @param domainClasses this collection is modified to add additional classes
     */
    private void addAdditionalBuildClasses(List<GrailsDomainClass> domainClasses) {
        def additionalClasses = []
        domainClasses.each {
            additionalClasses.addAll resolveAdditionalBuildClasses(it.fullName)
        }
        domainClasses.addAll(additionalClasses)
    }

    private List<GrailsDomainClass> resolveAdditionalBuildClasses(String domainName, List<GrailsDomainClass> circularList = []) {
        def classes = []
        def eagerLoad = TestDataConfigurationHolder.getUnitAdditionalBuildFor(domainName)
        if (eagerLoad) {
            for (eagerDomain in eagerLoad) {
                // Prevent looping forever
                String eagerDomainName = eagerDomain instanceof Class ? (eagerDomain as Class).name : eagerDomain.toString()
                if (!circularList.find { it.fullName == eagerDomainName }) {
                    classes.addAll resolveAdditionalBuildClasses(eagerDomainName, classes)
                }
            }
            classes.addAll(eagerLoad.collect { property ->
                if (!(property instanceof Class)) {
                    property = grailsApplication.classLoader.loadClass(property as String)
                }
                getGrailsDomainClass(property as Class)
            })
        }
        classes
    }
}
