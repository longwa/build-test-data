package grails.buildtestdata.handler

import groovy.transform.CompileStatic

@CompileStatic
class UrlConstraintHandler extends AbstractHandler {

    @Override
    void handle(Object instance, String propertyName) {
        setValue(instance,propertyName,'http://www.example.com')
    }
}
