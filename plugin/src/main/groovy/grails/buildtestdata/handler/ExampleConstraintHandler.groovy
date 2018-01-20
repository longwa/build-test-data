package grails.buildtestdata.handler

import grails.buildtestdata.builders.DataBuilderContext
import grails.buildtestdata.utils.IsoDateUtil
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.time.LocalDate
import java.time.LocalDateTime

@Slf4j
@CompileStatic
class ExampleConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(Object instance, String propertyName, Constraint appliedConstraint,
                ConstrainedProperty constrainedProperty, DataBuilderContext ctx) {
        Object exValue = getExampleContraintValue(constrainedProperty)
        if(exValue != null){
            setProperty(instance, propertyName, exValue)
        }
    }

    @CompileDynamic
    Object getExampleContraintValue(constrainedProperty){
        Object exValue = constrainedProperty.metaConstraints["example"]
        if(exValue == null) return null

        if (exValue instanceof String) {
            String sval = exValue as String
            Class typeToConvertTo = constrainedProperty.propertyType

            if (Date.isAssignableFrom(typeToConvertTo)) {
                exValue = IsoDateUtil.parse(sval)
            } else if (LocalDate.isAssignableFrom(typeToConvertTo)) {
                exValue = IsoDateUtil.parseLocalDate(sval)
                //LocalDate.parse(val, DateTimeFormatter.ISO_DATE_TIME)
            } else if (LocalDateTime.isAssignableFrom(typeToConvertTo)) {
                exValue = IsoDateUtil.parseLocalDateTime(sval)
            } else if (Number.isAssignableFrom(typeToConvertTo)) {
                exValue = sval.asType(typeToConvertTo)
            }
        }

        return exValue
    }

}
