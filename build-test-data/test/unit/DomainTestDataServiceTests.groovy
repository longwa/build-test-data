import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.ObjectError
import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.handler.MinConstraintHandler
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.Constraint
import org.codehaus.groovy.grails.validation.MinConstraint
import grails.buildtestdata.handler.MinSizeConstraintHandler
import grails.buildtestdata.handler.MaxConstraintHandler
import grails.buildtestdata.handler.RangeConstraintHandler
import grails.buildtestdata.handler.InListConstraintHandler
import grails.buildtestdata.handler.NullableConstraintHandler
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.DomainInstanceBuilder
import org.codehaus.groovy.grails.commons.GrailsClass;

import grails.buildtestdata.MockErrors


class DomainTestDataServiceTests extends GroovyTestCase {

    void testMinCalendar() {
        def calendar = new GregorianCalendar()
        def minCalendar = new GregorianCalendar()
        minCalendar.add(Calendar.DATE, 100)
        def domainObject = [testProperty: calendar]
        new MinConstraintHandler().handle( domainObject, 'testProperty', [ minValue: minCalendar ] )
        assertEquals minCalendar, domainObject.testProperty
    }

    void testMinEmail() {
        def email = 'a@b.com'
        def domainObject = [testProperty: email]
        assertNotNull domainObject.testProperty
        def appliedConstraint = new Expando()
        appliedConstraint.setProperty('name', ConstrainedProperty.EMAIL_CONSTRAINT)
        def constrainedProperty = [appliedConstraints:[appliedConstraint]]
        assertEquals constrainedProperty.appliedConstraints.find {it.name == ConstrainedProperty.EMAIL_CONSTRAINT}.name, ConstrainedProperty.EMAIL_CONSTRAINT
        new MinSizeConstraintHandler().handle(domainObject, 'testProperty', [minSize:100], constrainedProperty)
        assertNotNull domainObject.testProperty
        assertEquals domainObject.testProperty.size(), 100
    }

    void testMinUrl() {
        def url = 'http://www.carol.com'
        def domainObject = [testProperty: url]
        assertNotNull domainObject.testProperty
        def appliedConstraint = new Expando()
        appliedConstraint.setProperty('name', ConstrainedProperty.URL_CONSTRAINT)
        def constrainedProperty = [appliedConstraints:[appliedConstraint]]
        assertEquals constrainedProperty.appliedConstraints.find {it.name == ConstrainedProperty.URL_CONSTRAINT}.name, ConstrainedProperty.URL_CONSTRAINT
        new MinSizeConstraintHandler().handle(domainObject, 'testProperty', [minSize:100], constrainedProperty)
        assertNotNull domainObject.testProperty
        assertEquals domainObject.testProperty.size(), 100
    }

    void testMaxCalendar() {
        def calendar = new GregorianCalendar()
        def maxCalendar = new GregorianCalendar()
        maxCalendar.add(Calendar.DATE, -100)
        def domainObject = [testProperty: calendar]
        new MaxConstraintHandler().handle(domainObject, 'testProperty', [maxValue:maxCalendar])
        assertEquals maxCalendar, domainObject.testProperty
    }

    void testRangeCalendar() {
        def calendar = new GregorianCalendar()
        def maxCalendar = new GregorianCalendar()
        maxCalendar.add(Calendar.DATE, -100)
        def minCalendar = new GregorianCalendar()
        minCalendar.add(Calendar.DATE, 100)
        def domainObject = [testProperty: calendar]
        new RangeConstraintHandler().handle(domainObject, 'testProperty', [range:[from:minCalendar, to:maxCalendar]])
        assertEquals minCalendar, domainObject.testProperty
    }

    void testInListCalendar() {
        def calendar = new GregorianCalendar()
        def maxCalendar = new GregorianCalendar()
        maxCalendar.add(Calendar.DATE, -100)
        def minCalendar = new GregorianCalendar()
        minCalendar.add(Calendar.DATE, 100)
        def domainObject = [testProperty: calendar]
        new InListConstraintHandler().handle(domainObject, 'testProperty', [list:[minCalendar, maxCalendar]])
        assertEquals minCalendar, domainObject.testProperty
    }

    void testUnhandledProperty() {
        String[] s = []
        def domainInstance = [testProperty: s]
        shouldFail {buildTestDataService.createMissingProperty(domainInstance, 'testProperty', [propertyType: String[].class])}
    }

    void testWeirdConstraint() {
        def mockValidate = {target, propertyValue, MockErrors errors -> errors.addError(new ObjectError("foo", "foo")) }
        def mockConstrainedProperty = [name: "weirdConstraint", validate: mockValidate, appliedConstraints: [[name: "weirdConstraint"]]]
        def mockDomainInstance = [propertyName: "testProperty"]

        def mockDomainArtefact = [clazz: "domainClass", constrainedProperties: [:]]

        def domainInstanceBuilder = new DomainInstanceBuilder(mockDomainArtefact)
        // tests if we have an unknown constraint that fails validation that we return false
        assertFalse( domainInstanceBuilder.createProperty(mockDomainInstance, 'testProperty', mockConstrainedProperty, [:]) == true )
    }


}
