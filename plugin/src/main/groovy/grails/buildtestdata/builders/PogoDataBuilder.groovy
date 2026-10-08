package grails.buildtestdata.builders

import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.utils.Basics
import grails.buildtestdata.utils.DomainUtil
import grails.databinding.DataBinder
import grails.databinding.SimpleMapDataBindingSource
import grails.databinding.errors.BindingError
import grails.databinding.events.DataBindingListenerAdapter
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Slf4j
@CompileStatic
class PogoDataBuilder implements DataBuilder {

    @Order(Ordered.LOWEST_PRECEDENCE)
    static class Factory implements DataBuilderFactory<PogoDataBuilder> {
        @Override
        PogoDataBuilder build(Class target) {
            return new PogoDataBuilder(target)
        }

        @Override
        boolean supports(Class clazz) {
            return true
        }
    }

    DataBinder dataBinder
    Class targetClass

    PogoDataBuilder(Class targetClass) {
        // findConcreteSubclass takes care of subtituing in concrete classes for abstracts
        this.targetClass = DomainUtil.findConcreteSubclass(targetClass)
        this.dataBinder = new TestDataBinder()
    }

    @Override
    def build(DataBuilderContext ctx) {
        return build([:], ctx)
    }

    @Override
    def build(Map args, DataBuilderContext ctx) {
        return doBuild(ctx)
    }

    def doBuild(DataBuilderContext ctx) {
        // Nothing to do, target exists already
        if (ctx.target) {
            return ctx.target
        }

        // Create a new empty instance
        def instance = getNewInstance()

        Map initialProps = findMissingConfigValues(ctx.data, instance)
        if (initialProps) {
            if (ctx.data) {
                ctx.data = [:] + initialProps + ctx.data
            }
            else {
                ctx.data = [:] + initialProps
            }
        }
        if (ctx.data) {
            warnUnknownProperties(instance, ctx.data)
            bindData(instance, ctx.data)
        }

        instance
    }

    /**
     * The data binder skips values for properties that don't exist, let the user know in case it's a typo
     */
    void warnUnknownProperties(Object instance, Map<String, ?> data) {
        for (String key in findUnknownProperties(instance, data)) {
            log.warn("Ignoring '{}' when building {}, it is not a property of the class", key, targetClass.name)
        }
    }

    Set<String> findUnknownProperties(Object instance, Map<String, ?> data) {
        data.keySet().findAll { String key ->
            // indexed properties such as books[0] bind to the books property
            String propertyName = key.contains('[') ? key.substring(0, key.indexOf('[')) : key
            instance.metaClass.getMetaProperty(propertyName) == null
        }
    }

    /**
     * The data binder skips a value that can't be converted to its property's type, leaving the property unset, so
     * fail instead of building an instance without it. Setting failOnBindingError = false in TestDataConfig logs the
     * values instead, to ease migrating tests that relied on them being skipped.
     */
    void bindData(Object instance, Map<String, ?> data) {
        BindingErrorCollector collector = new BindingErrorCollector()
        dataBinder.bind(instance, new SimpleMapDataBindingSource(data), collector)
        if (!collector.bindingErrors) {
            return
        }

        String details = collector.bindingErrors.collect { BindingError error -> describeBindingError(error) }.join('\n')
        if (TestDataConfigurationHolder.failOnBindingError) {
            throw new IllegalArgumentException(
                "Unable to build ${targetClass.name}, the following values could not be bound:\n$details",
                collector.bindingErrors.first().cause
            )
        }
        log.warn("Ignoring values that could not be bound when building {}:\n{}", targetClass.name, details)
    }

    static String describeBindingError(BindingError error) {
        Object value = error.rejectedValue
        Class propertyType = error.object.metaClass.getMetaProperty(error.propertyName)?.type
        "  - ${error.object.getClass().name}.${error.propertyName} (${propertyType?.name}) cannot be set to " +
            "'${value}' (${value?.getClass()?.name}): ${error.cause}"
    }

    static class BindingErrorCollector extends DataBindingListenerAdapter {
        List<BindingError> bindingErrors = []

        @Override
        void bindingError(BindingError error, Object errors) {
            bindingErrors << error
        }
    }

    Map<String, Object> findMissingConfigValues(Map propValues, Object newInstance) {
        Set<String> missingProperties = TestDataConfigurationHolder.getConfigPropertyNames(targetClass.name) - propValues.keySet()
        TestDataConfigurationHolder.getPropertyValues(targetClass.name, newInstance, missingProperties, propValues)
    }

    def getNewInstance() {
        if (List.isAssignableFrom(targetClass)) {
            [] as List
        }
        else if (Set.isAssignableFrom(targetClass)) {
            [] as Set
        }
        else if (targetClass.isEnum()) {
            Basics.getDefaultValue(targetClass)
        }
        else {
            targetClass.newInstance()
        }
    }
}
