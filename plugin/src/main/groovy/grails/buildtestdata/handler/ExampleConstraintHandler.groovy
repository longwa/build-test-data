package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.DomainInstanceRegistry
import grails.buildtestdata.utils.IsoDateUtil
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.datastore.mapping.model.types.ToMany
import org.grails.datastore.mapping.reflect.ClassPropertyFetcher

import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

import static grails.buildtestdata.DomainUtil.getPersistentEntity
import static grails.buildtestdata.DomainUtil.propertyIsDomainClass
import static grails.buildtestdata.TestDataConfigurationHolder.getConfigPropertyNames
import static grails.buildtestdata.TestDataConfigurationHolder.getPropertyValues

@Slf4j
@CompileStatic
class ExampleConstraintHandler extends AbstractConstraintHandler {
    @Override
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList) {
        Object exValue = getExampleContraintValue(constrainedProperty)
        if(exValue != null){
            setProperty(domain, propertyName, exValue)
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
