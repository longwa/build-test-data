package grails.buildtestdata.mixin

import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.DomainUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.runtime.TestRuntime
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.plugins.web.ControllersGrailsPlugin
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter

import java.lang.reflect.Modifier

/**
 * Helper class for resolving all of the required domain objects for a given initial set of
 * domain objects.
 *
 * @since 3.0.0
 */
@CompileStatic
class MockDomainHelper {
    private static Map<String, List<GrailsDomainClass>> subClassCache = [:]

    private GrailsApplication grailsApplication
    private TestRuntime runtime

    MockDomainHelper(TestRuntime runtime) {
        this.runtime = runtime
        this.grailsApplication = runtime.getValue("grailsApplication", GrailsApplication)

        DomainUtil.grailsApplication = this.grailsApplication
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
     * For simple domain class tests, perform the traditional mocking on DomainClassUnitTestMixin
     *
     * @param testInstance
     * @param classes
     */
    void mockDomains(Object testInstance, Collection<Class> classes = null) {
        if (!classes) {
            classes = runtime.getValueIfExists(GormProvider.DomainClass.registeredName) as Set<Class>
        }
        if(classes) {
            testInstance.invokeMethod("mockDomains", classes as Class[])
            runtime.putValue(GormProvider.DomainClass.initializedName, classes)
        }
    }

    /**
     * Registers the given classes for mocking.
     *
     * @param testClass
     * @param classes
     */
    void registerPersistentClasses(Collection<Class> classes) {
        Collection<Class> allClasses = []
        allClasses.addAll(classes)

        // Add any existing that were added directly via @Domain
        Set<Class> existing = runtime.getValueIfExists(gormProvider.registeredName) as Set<Class>
        if(existing) {
            allClasses.addAll(existing)
        }
        if(allClasses) {
            runtime.putValue(gormProvider.registeredName, allClasses)
        }
    }

    /**
     * Mixin the build test data build() methods
     * @param classes list of classes to decorate. If not given, the list of initialized classes is pulled from the context
     */
    void decorate(Collection<Class> classes = null) {
        BuildTestDataService buildTestDataService = new BuildTestDataService()
        if (!classes) {
            classes = runtime.getValueIfExists(gormProvider.initializedName) as Set<Class>
        }
        classes?.each { Class clazz ->
            GrailsDomainClass domainClass = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, clazz.name) as GrailsDomainClass
            assert domainClass, "Domain class ${clazz.name} should be registered but is missing"
            buildTestDataService.decorateWithMethods(domainClass)
        }
    }

    GormProvider getGormProvider() {
        GormProvider.lookup(runtime)
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
            if(it.isAbstract()) {
                subClasses.addAll(findDomainSubclasses(it))
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
        if(!isAbstract) {
            domainClass = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, clazz.name) as GrailsDomainClass
        }
        if(!domainClass || isAbstract) {
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
        GrailsDomainClass domain = (GrailsDomainClass) grailsApplication.addArtefact(DomainClassArtefactHandler.TYPE, domainClassToMock)
        MetaClass mc = GrailsClassUtils.getExpandoMetaClass(domainClassToMock)
        ControllersGrailsPlugin.enhanceDomainWithBinding(grailsApplication.mainContext, domain, mc)
        return domain
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
            if(property?.association && !constrainedProperty.nullable) {
                Class type = property.referencedPropertyType ?: property.type

                // If this is a domain class and it's not already been registered, register it and add to the list
                if(type && DomainClassArtefactHandler.isDomainClass(type)) {
                    domains << getGrailsDomainClass(type)
                }
            }
        }

        // Recursively find all required. Make sure we only look at each domain once
        domains.each {
            if(!requiredDomains.containsKey(it.fullName)) {
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
    private List<GrailsDomainClass> findDomainSubclasses(GrailsDomainClass domainClass) {
        // Finding subclasses is expensive, only do this once if possible for the whole run
        List<GrailsDomainClass> subClassList = []
        if(subClassCache.containsKey(domainClass.fullName)) {
            subClassList = subClassCache[domainClass.fullName]
        }
        else {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true)
            provider.addIncludeFilter(new AssignableTypeFilter(domainClass.getClazz()))

            // Scan all packages
            Set<BeanDefinition> components = provider.findCandidateComponents("")
            for(BeanDefinition it in components) {
                Class subClass = grailsApplication.classLoader.loadClass(it.beanClassName)

                // If this is a domain class and it's not been registered, register it and setup subclasses
                if(DomainClassArtefactHandler.isDomainClass(subClass)) {
                    GrailsDomainClass domainSubClass = getGrailsDomainClass(subClass)
                    subClassList << domainSubClass
                }
            }
        }

        // Add to the domain class so that the domain instance builder can use it later
        domainClass.getSubClasses().addAll(subClassList)
        subClassCache[domainClass.fullName] = subClassList
        subClassList
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
        while(superClass) {
            if(DomainClassArtefactHandler.isDomainClass(superClass)) {
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
        if(eagerLoad) {
            for(eagerDomain in eagerLoad) {
                // Prevent looping forever
                String eagerDomainName = eagerDomain instanceof Class ? (eagerDomain as Class).name : eagerDomain.toString()
                if(!circularList.find { it.fullName == eagerDomainName }) {
                    classes.addAll resolveAdditionalBuildClasses(eagerDomainName, classes)
                }
            }
            classes.addAll(eagerLoad.collect { property ->
                if(!(property instanceof Class)) {
                    property = grailsApplication.classLoader.loadClass(property as String)
                }
                getGrailsDomainClass(property as Class)
            })
        }
        classes
    }
}

enum GormProvider {
    Hibernate("hibernate"),
    MongoDb("mongoDb"),
    DomainClass("domainClass")

    GormProvider(String prefix) { this.prefix = prefix }
    final String prefix

    String getInitializedName() {
        "initialized${this.name()}PersistentClasses"
    }

    String getRegisteredName() {
        "${prefix}PersistentClassesToRegister"
    }

    static GormProvider lookup(TestRuntime runtime) {
        if(runtime.features.contains("hibernateGorm")) {
            Hibernate
        }
        else if(runtime.features.contains("mongoDbGorm")) {
            MongoDb
        }
        else {
            DomainClass
        }
    }
}
