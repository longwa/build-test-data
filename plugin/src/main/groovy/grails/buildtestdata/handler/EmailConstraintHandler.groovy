package grails.buildtestdata.handler

import groovy.transform.CompileStatic

@CompileStatic
class EmailConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName) {
        setValue(instance,propertyName,'a@b.com')
    }
}
