package grails.buildtestdata.builders

import grails.databinding.SimpleDataBinder
import groovy.transform.CompileStatic

/**
 * SimpleDataBinder that reports a binding error for a String that is not one of the enum's constants, instead of
 * silently binding null.
 */
@CompileStatic
class TestDataBinder extends SimpleDataBinder {

    @Override
    protected convertStringToEnum(Class<? extends Enum> enumClass, String value) {
        Enum.valueOf((Class) enumClass, value)
    }
}
