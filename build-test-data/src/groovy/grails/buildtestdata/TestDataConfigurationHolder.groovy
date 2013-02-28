package grails.buildtestdata

import grails.util.GrailsUtil
import groovy.util.ConfigObject
import org.apache.commons.logging.LogFactory
import grails.buildtestdata.handler.NullableConstraintHandler

class TestDataConfigurationHolder {

    static log = LogFactory.getLog("grails.buildtestdata.TestDataConfigurationHolder")

    private static ConfigObject configFile
    private static Map sampleData

    private static ConfigSlurper configSlurper = new ConfigSlurper(GrailsUtil.environment)

    static sampleDataIndexer = [:]

    static reset() {
        loadTestDataConfig()
    }

    static loadTestDataConfig() {
        Class testDataConfigClass = getDefaultTestDataConfigClass()

        if (testDataConfigClass) {
            configFile = configSlurper.parse(testDataConfigClass)
            setSampleData( configFile?.testDataConfig?.sampleData as Map )
            log.debug "configFile loaded: ${configFile}"
        } else {
            setSampleData( [:] )
        }
    }

    static Class getDefaultTestDataConfigClass() {
        GroovyClassLoader classLoader = new GroovyClassLoader(TestDataConfigurationHolder.classLoader)
        try {
            return classLoader.loadClass('TestDataConfig')
        } catch (ClassNotFoundException ex) {
            log.warn "TestDataConfig.groovy not found, build-test-data plugin proceeding without config file"
        }
    }

    static boolean isPluginEnabled() {
        return !(configFile?.testDataConfig?.enabled == false)
    }

    static Map getSampleData() {
        if (sampleData == null) loadTestDataConfig()
        return sampleData
    }

    static setSampleData(String configObjectText) {
        setSampleData( configSlurper.parse(configObjectText) )
    }

    static setSampleData(Map configObjectMap) {
        sampleData = configObjectMap
        sampleDataIndexer = [:]
    }

    static getConfigFor(String domainName) {
        return sampleData."$domainName"
    }

    static getConfigPropertyNames(String domainName) {
        return getConfigFor(domainName)?.keySet() ?: []
    }

    static getSuppliedPropertyValue(propertyValues, domainName, propertyName) {
        return retrievePropertyValue(propertyValues, sampleData."$domainName"?."$propertyName")
    }

    private static retrievePropertyValue(propertyValues, Closure closure) {
        return closure.maximumNumberOfParameters > 0 ? closure.call(propertyValues) : closure.call()
    }

    private static retrievePropertyValue(propertyValues, value) {
        return value
    }

    static getPropertyValues(domainName, propertyNames, Map propertyValues = [:]) {
        for (propertyName in propertyNames) {
            propertyValues[propertyName] = getSuppliedPropertyValue(propertyValues, domainName, propertyName)
        }
        return propertyValues
    }
}
