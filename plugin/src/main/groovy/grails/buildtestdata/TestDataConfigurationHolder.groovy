package grails.buildtestdata

import grails.buildtestdata.utils.DomainUtil
import grails.util.Environment
import grails.util.Holders
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

@Slf4j
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
class TestDataConfigurationHolder {
    @Lazy
    private static Resource testDataConfigResource = { getDefaultTestDataConfigResource() }()

    @Lazy
    static TestDataConfiguration config = { new TestDataConfiguration(testDataConfigResource) }()

    static void loadTestDataConfig() {
        // Nothing to do here, loaded lazily on demand
    }

    static void reset() {
        config.initialize(testDataConfigResource)
    }

    static Resource getDefaultTestDataConfigResource() {
        String configPath = Holders.flatConfig['grails.buildtestdata.testDataConfig'] ?: 'TestDataConfig.groovy'
        if (!configPath.endsWith(".groovy")) {
            configPath += ".groovy"
        }
        ClassPathResource resource = new ClassPathResource(configPath, TestDataConfigurationHolder.classLoader)
        if (!resource.exists()) {
            return null
        }
        resource
    }

    static void setSampleData(Map<String, Object> sampleData) {
        config.sampleData = sampleData
    }

    static Map<String, Object> getConfigFor(String domainName) {
        config.getConfigFor(domainName)
    }

    static List<Class> getUnitAdditionalBuildFor(String domainName) {
        config.getUnitAdditionalBuildFor(domainName)
    }

    static Class getAbstractDefaultFor(String domainName) {
        config.getAbstractDefaultFor(domainName)
    }

    static Set<String> getConfigPropertyNames(String domainName) {
        config.getConfigPropertyNames(domainName)
    }

    static Object getSuppliedPropertyValue(Map<String, Object> propertyValues, String domainName, String propertyName) {
        config.getSuppliedPropertyValue(propertyValues, domainName, propertyName)
    }

    static Map<String, Object> getPropertyValues(String domainName, Object newInstance, Set<String> propertyNames, Map<String, Object> propertyValues = [:]) {
        config.getPropertyValues(domainName, newInstance, propertyNames, propertyValues)
    }

    /**
     * Takes a testDataConfig { ... } block that is merged over the default values
     * @param block
     */
    static void mergeConfig(Closure block) {
        ConfigObject blockConfig = new ConfigSlurper(Environment.current.name).parse(new ClosureScript(closure: block))
        config.mergeConfig(blockConfig)
    }

    @CompileStatic
    private static class ClosureScript extends Script {
        Closure closure
        def run() {
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.delegate = this
            closure.call()
        }
    }
}

@CompileStatic
@Slf4j
class TestDataConfiguration {
    final ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)

    Map<String, Object> sampleData
    Map<String, List<Class>> unitAdditionalBuild
    Map<String, Class> abstractDefault

    TestDataConfiguration(Resource testDataConfigResource) {
        initialize(testDataConfigResource)
    }

    void initialize(Resource testDataConfigResource) {
        sampleData = [:]
        unitAdditionalBuild = [:]
        abstractDefault = [:]

        if (testDataConfigResource) {
            ConfigObject configFile = configSlurper.parse(testDataConfigResource.URL)
            log.debug("Loading configuration from file: {}", configFile)
            mergeConfig(configFile)
        }
    }

    void mergeConfig(ConfigObject config) {
        // Add to any existing configuration
        sampleData += config['testDataConfig']['sampleData'] as Map ?: [:]
        unitAdditionalBuild += config['testDataConfig']['unitAdditionalBuild'] as Map ?: [:]
        abstractDefault += config['testDataConfig']['abstractDefault'] as Map ?: [:]

        // If we have abstract defaults, automatically add transitive dependencies
        // for them since they may need to be built.
        abstractDefault.each { String key, Class value ->
            if (DomainUtil.isAbstract(value)) {
                throw new IllegalArgumentException("Value for 'abstractDefault.${key}' must be a concrete class")
            }
            if (unitAdditionalBuild.containsKey(key)) {
                unitAdditionalBuild[key] << value
            }
            else {
                unitAdditionalBuild[key] = [value]
            }
        }
    }

    Map<String, Object> getConfigFor(String domainName) {
        sampleData[domainName] as Map
    }

    List<Class> getUnitAdditionalBuildFor(String domainName) {
        unitAdditionalBuild[domainName] ?: [] as List<Class>
    }

    Class getAbstractDefaultFor(String domainName) {
        abstractDefault[domainName]
    }

    Set<String> getConfigPropertyNames(String domainName) {
        getConfigFor(domainName)?.keySet() ?: [] as Set<String>
    }

    Object getSuppliedPropertyValue(Map<String, Object> propertyValues, String domainName, String propertyName, Object newInstance = null) {
        if (!sampleData[domainName]) {
            throw new IllegalArgumentException("Sample data for $domainName does not exist")
        }

        // Fetch both and either invoke the closure or just return raw values
        Object value = sampleData[domainName][propertyName]
        if (!(value instanceof Closure)) {
            return value
        }

        Closure block = value as Closure
        switch(block.maximumNumberOfParameters) {
            case 1:
                return block.call(propertyValues)
            case 2:
                return block.call(propertyValues, newInstance)
            default:
                return block.call()
        }
    }

    Map<String, Object> getPropertyValues(String domainName, Object newInstance, Set<String> propertyNames, Map<String, Object> propertyValues = [:]) {
        for (propertyName in propertyNames) {
            propertyValues[propertyName] = getSuppliedPropertyValue(propertyValues, domainName, propertyName, newInstance)
        }
        return propertyValues
    }
}
