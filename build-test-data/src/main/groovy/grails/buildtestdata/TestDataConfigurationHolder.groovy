package grails.buildtestdata

import grails.util.Environment
import grails.util.Holders
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class TestDataConfigurationHolder {
    private static ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
    private static ConfigObject  configFile

    static Map<String, Object> sampleData = [:]
    static Map<String, List> unitAdditionalBuild
    static Map<String, Class> abstractDefault

    static void reset() {
        loadTestDataConfig()
    }

    static void loadTestDataConfig() {
        Class testDataConfigClass = getDefaultTestDataConfigClass()
        if (testDataConfigClass) {
            configFile = configSlurper.parse(testDataConfigClass)

            // Process the sample data
            setSampleData(configFile['testDataConfig']['sampleData'] as Map ?: [:])

            // Process additional build for unit testing
            unitAdditionalBuild = configFile['testDataConfig']['unitAdditionalBuild'] as Map ?: [:]

            // If we have abstract defaults, automatically add transitive dependencies
            // for them since they may need to be built.
            abstractDefault = configFile['testDataConfig']['abstractDefault'] as Map ?: [:]
            abstractDefault.each { String key, Class value ->
                if (DomainUtil.isAbstract(value)) {
                    throw new IllegalArgumentException("Default value for 'abstractDefault.${key}' must be a concrete class")
                }

                if (unitAdditionalBuild.containsKey(key)) {
                    unitAdditionalBuild[key] << value
                }
                else {
                    unitAdditionalBuild[key] = [value]
                }
            }

            log.debug("Loaded configuration file: {}", configFile)
        }
    }

    static Class getDefaultTestDataConfigClass() {
        GroovyClassLoader classLoader = new GroovyClassLoader(TestDataConfigurationHolder.classLoader)
        String testDataConfig = Holders.flatConfig['grails.buildtestdata.testDataConfig'] ?: 'TestDataConfig'
        try {
            return classLoader.loadClass(testDataConfig)
        }
        catch (ClassNotFoundException ignored) {
            log.warn("{}.groovy not found, build-test-data plugin proceeding without config file", testDataConfig)
            return null
        }
    }

    static void setSampleData(Object configObject) {
        if (configObject instanceof String) {
            sampleData = configSlurper.parse(configObject as String) as Map
        }
        else if (configObject instanceof Map) {
            sampleData = configObject as Map
        }
        else {
            throw new IllegalArgumentException("TestDataConfigurationHolder.sampleData should be either a String or a Map")
        }
    }

    static Map<String, Object> getConfigFor(String domainName) {
        sampleData[domainName] as Map
    }

    static List getUnitAdditionalBuildFor(String domainName) {
        unitAdditionalBuild[domainName]
    }

    static Class getAbstractDefaultFor(String domainName) {
        abstractDefault[domainName]
    }

    static Set<String> getConfigPropertyNames(String domainName) {
        getConfigFor(domainName)?.keySet() ?: [] as Set<String>
    }

    static Object getSuppliedPropertyValue(Map<String, Object> propertyValues, String domainName, String propertyName) {
        // Fetch both and either invoke the closure or just return raw values
        Object value = sampleData[domainName][propertyName]
        if (value instanceof Closure) {
            Closure block = value as Closure
            return block.maximumNumberOfParameters > 0 ? block.call(propertyValues) : block.call()
        }

        value
    }

    static Map<String, Object> getPropertyValues(String domainName, Set<String> propertyNames, Map<String, Object> propertyValues = [:]) {
        for (propertyName in propertyNames) {
            propertyValues[propertyName] = getSuppliedPropertyValue(propertyValues, domainName, propertyName)
        }
        return propertyValues
    }
}
