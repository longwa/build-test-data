package grails.buildtestdata.handler

import groovy.transform.CompileStatic

@CompileStatic
class ConstraintHandlerException extends Exception {
    ConstraintHandlerException(String message) {
        super(message)
    }
}
