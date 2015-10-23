import grails.buildtestdata.BuildTestDataService
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.MockErrors
import grails.buildtestdata.handler.InListConstraintHandler
import grails.buildtestdata.handler.MaxConstraintHandler
import grails.buildtestdata.handler.MinConstraintHandler
import grails.buildtestdata.handler.MinSizeConstraintHandler
import grails.buildtestdata.handler.RangeConstraintHandler
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.validation.ConstrainedProperty
import org.junit.Before
import org.junit.Test
import org.springframework.validation.ObjectError

@TestMixin(GrailsUnitTestMixin)
class DomainTestDataServiceTests {
    def buildTestDataService

    @Before
    void setUp() {
        buildTestDataService = new BuildTestDataService()
    }

    @Test
    void testMinCalendar() {
        def calendar = new GregorianCalendar()
        def minCalendar = new GregorianCalendar()
        minCalendar.add(Calendar.DATE, 100)
        def domainObject = [testProperty: calendar]
        new MinConstraintHandler().handle(domainObject, 'testProperty', [minValue: minCalendar])
        assert domainObject.testProperty == minCalendar
    }

    @Test
    void testMinEmail() {
        def email = 'a@b.com'
        def domainObject = [testProperty: email]
        assert domainObject.testProperty
        def appliedConstraint = new Expando()
        appliedConstraint.setProperty('name', ConstrainedProperty.EMAIL_CONSTRAINT)
        def constrainedProperty = [appliedConstraints: [appliedConstraint]]
        assert ConstrainedProperty.EMAIL_CONSTRAINT == constrainedProperty.appliedConstraints.find { it.name == ConstrainedProperty.EMAIL_CONSTRAINT }.name
        new MinSizeConstraintHandler().handle(domainObject, 'testProperty', [minSize: 100], constrainedProperty)
        assert domainObject.testProperty
        assert 100 == domainObject.testProperty.size()
    }

    @Test
    void testMinUrl() {
        def url = 'http://www.carol.com'
        def domainObject = [testProperty: url]
        assert domainObject.testProperty
        def appliedConstraint = new Expando()
        appliedConstraint.setProperty('name', ConstrainedProperty.URL_CONSTRAINT)
        def constrainedProperty = [appliedConstraints: [appliedConstraint]]
        assert ConstrainedProperty.URL_CONSTRAINT == constrainedProperty.appliedConstraints.find { it.name == ConstrainedProperty.URL_CONSTRAINT }.name
        new MinSizeConstraintHandler().handle(domainObject, 'testProperty', [minSize: 100], constrainedProperty)
        assert domainObject.testProperty
        assert 100 == domainObject.testProperty.size()
    }

    @Test
    void testMaxCalendar() {
        def calendar = new GregorianCalendar()
        def maxCalendar = new GregorianCalendar()
        maxCalendar.add(Calendar.DATE, -100)
        def domainObject = [testProperty: calendar]
        new MaxConstraintHandler().handle(domainObject, 'testProperty', [maxValue: maxCalendar])
        assert domainObject.testProperty == maxCalendar
    }

    @Test
    void testRangeCalendar() {
        def calendar = new GregorianCalendar()
        def maxCalendar = new GregorianCalendar()
        maxCalendar.add(Calendar.DATE, -100)
        def minCalendar = new GregorianCalendar()
        minCalendar.add(Calendar.DATE, 100)
        def domainObject = [testProperty: calendar]
        new RangeConstraintHandler().handle(domainObject, 'testProperty', [range: [from: minCalendar, to: maxCalendar]])
        assert domainObject.testProperty == minCalendar
    }

    @Test
    void testInListCalendar() {
        def calendar = new GregorianCalendar()
        def maxCalendar = new GregorianCalendar()
        maxCalendar.add(Calendar.DATE, -100)
        def minCalendar = new GregorianCalendar()
        minCalendar.add(Calendar.DATE, 100)
        def domainObject = [testProperty: calendar]
        new InListConstraintHandler().handle(domainObject, 'testProperty', [list: [minCalendar, maxCalendar]])
        assert domainObject.testProperty == minCalendar
    }

    @Test
    void testUnhandledProperty() {
        String[] s = []
        def domainInstance = [testProperty: s]
        shouldFail { buildTestDataService.createMissingProperty(domainInstance, 'testProperty', [propertyType: String[].class]) }
    }

    @Test
    void testWeirdConstraint() {
        def mockValidate = { target, propertyValue, MockErrors errors -> errors.addError(new ObjectError("foo", "foo")) }
        def mockConstrainedProperty = [name: "weirdConstraint", validate: mockValidate, appliedConstraints: [[name: "weirdConstraint"]]]
        def mockDomainInstance = [propertyName: "testProperty"]
        def mockDomainArtefact = [clazz: "domainClass", constrainedProperties: [:]]
        def domainInstanceBuilder = new DomainInstanceBuilder(mockDomainArtefact)

        // Tests if we have an unknown constraint that fails validation that we return false
        assert !(domainInstanceBuilder.createProperty(mockDomainInstance, 'testProperty', mockConstrainedProperty, [:]) == true)
    }
}
