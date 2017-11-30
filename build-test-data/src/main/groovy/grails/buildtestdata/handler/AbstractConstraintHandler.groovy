package grails.buildtestdata.handler

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper

@CompileStatic
abstract class AbstractConstraintHandler implements ConstraintHandler {
    void setProperty(Object target, String propertyName, Object value) {
        InvokerHelper.setProperty(target, propertyName, value)
    }

    Object getProperty(Object target, String propertyName) {
        InvokerHelper.getProperty(target, propertyName)
    }
}
