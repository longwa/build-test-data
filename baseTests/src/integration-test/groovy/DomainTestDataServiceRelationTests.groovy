import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Test

@TestMixin(IntegrationTestMixin)
class DomainTestDataServiceRelationTests extends DomainTestDataServiceBase {
    void setUp() {
        Owner.metaClass.validate = validateMock
        Owned.metaClass.validate = validateMock
        EmbeddedOwner.metaClass.validate = validateMock
        CircularSelf.metaClass.validate = validateMock
        CircularOne.metaClass.validate = validateMock
        CircularTwo.metaClass.validate = validateMock

        Owner.metaClass.save = saveMock
        Owned.metaClass.save = saveMock
        EmbeddedOwner.metaClass.save = saveMock
        CircularSelf.metaClass.save = saveMock
        CircularOne.metaClass.save = saveMock
        CircularTwo.metaClass.save = saveMock

        Owner.metaClass.ident = identMock
        Owned.metaClass.ident = identMock
        EmbeddedOwner.metaClass.ident = identMock
        CircularSelf.metaClass.ident = identMock
        CircularOne.metaClass.ident = identMock
        CircularTwo.metaClass.ident = identMock
    }

    @Test
    void testCascadingBuildNonCircular() {
        def domainArtefact = registerDomainClass(Owner)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(Owned)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def domainObject = Owner.build()
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.owned.testProperty != null
    }

    @Test
    void testBuildCircularSelfReferential() {
        def domainArtefact = registerDomainClass(CircularSelf)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def circDomainSelf = new DefaultGrailsDomainClass(CircularSelf)
        def domainProp = circDomainSelf.properties.find { it.name == 'circularSelf' }
        assert domainProp.isOneToOne()

        def domainObject = CircularSelf.build()
        assert domainObject != null
        assert domainObject.circularSelf == domainObject
    }

    @Test
    void testCascadingBuildCircularTwoClasses() {
        def domainArtefact = registerDomainClass(CircularOne)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(CircularTwo)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def circDomainOne = new DefaultGrailsDomainClass(CircularOne)
        def domainProp = circDomainOne.properties.find { it.name == 'circularTwo' }
        assert domainProp.isOneToOne()

        def circDomainTwo = new DefaultGrailsDomainClass(CircularTwo)
        domainProp = circDomainTwo.properties.find { it.type == CircularOne }
        assert domainProp.name == 'circularOne'

        def domainObject = CircularOne.build()
        assert domainObject != null
        assert domainObject.circularTwo.circularOne == domainObject
    }

    @Test
    void testCascadingBuildCircularTwoClassesWithChildClass() {
        def domainArtefact = registerDomainClass(CircularOne)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(CircularOneChild)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(CircularTwo)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def circDomainOne = new DefaultGrailsDomainClass(CircularOne)
        def domainProp = circDomainOne.properties.find { it.name == 'circularTwo' }
        assert domainProp.isOneToOne()

        def circDomainOneChild = new DefaultGrailsDomainClass(CircularOneChild)
        domainProp = circDomainOneChild.properties.find { it.name == 'circularTwo' }
        assert domainProp.isOneToOne()

        def circDomainTwo = new DefaultGrailsDomainClass(CircularTwo)
        domainProp = circDomainTwo.properties.find { it.type == CircularOne }
        assert domainProp.name == 'circularOne'

        def domainObject = CircularOneChild.build()
        assert domainObject != null
        assert domainObject.circularTwo.circularOne == domainObject
    }

    @Test
    void testEmbeddedProperties() {
        def domainArtefact = registerDomainClass(EmbeddedOwner)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(Owned)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def domainObject = EmbeddedOwner.build('owned.testProperty': 'I am embedded')
        assert domainObject != null
        assert domainObject.testProperty != null
        assert domainObject.owned.testProperty != null
        assert domainObject.owned.testProperty == 'I am embedded'

        try {
            EmbeddedOwner.build('tranProp': 3)   //setting readonly property
            fail()
        }
        catch (ignored) {
        }
    }
}

abstract class BaseMock {
    def validate = { return true }
    def ident = { delegate.id }
    def save = { options = [:] ->
        delegate.id = 1
        return true
    }
}

class Owner extends BaseMock {
    Long id
    Long version
    String testProperty
    Owned owned

    public static create() { Owner.newInstance() }
}

class EmbeddedOwner extends BaseMock {
    Long id
    Long version
    String testProperty

    int getTranProp() { 5 }

    static embedded = ['owned']
    static transients = ['tranProp']
    static constraints = {
        tranProp(nullable: true)
    }

    Owned owned = new Owned()

    public static create() { EmbeddedOwner.newInstance() }
}


class Owned extends BaseMock {
    Long id
    Long version
    String testProperty

    public static create() { Owned.newInstance() }
}

class CircularSelf extends BaseMock {
    Long id
    Long version
    CircularSelf circularSelf
    static constraints = {
        circularSelf(nullable: false)
    }

    public static create() { CircularSelf.newInstance() }
}

class CircularOne extends BaseMock {
    Long id
    Long version
    CircularTwo circularTwo

    public static create() { CircularOne.newInstance() }
}

class CircularOneChild extends CircularOne {
    public static create() { CircularOneChild.newInstance() }
}

class CircularTwo extends BaseMock {
    Long id
    Long version
    CircularOne circularOne

    public static create() { CircularTwo.newInstance() }
}
