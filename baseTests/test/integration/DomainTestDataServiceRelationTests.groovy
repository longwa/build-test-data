import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
class DomainTestDataServiceRelationTests extends DomainTestDataServiceBase {
	
    protected void setUp() {
        super.setUp()
        
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

        Owner.metaClass.static.create = {-> Owner.newInstance() }
        Owned.metaClass.static.create = {-> Owned.newInstance() }
        EmbeddedOwner.metaClass.static.create = {-> EmbeddedOwner.newInstance() }
        CircularSelf.metaClass.static.create = {-> CircularSelf.newInstance() }
        CircularOne.metaClass.static.create = {-> CircularOne.newInstance() }
        CircularTwo.metaClass.static.create = {-> CircularTwo.newInstance() }
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCascadingBuildNonCircular() {
        def domainArtefact = registerDomainClass(Owner)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(Owned)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def domainObject = Owner.build()
        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertNotNull domainObject.owned.testProperty
    }

    void testBuildCircularSelfReferential() {
        def domainArtefact = registerDomainClass(CircularSelf)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def circDomainSelf = new DefaultGrailsDomainClass(CircularSelf)
        def domainProp = circDomainSelf.properties.find {it.name == 'circularSelf' }
        assertTrue domainProp.isOneToOne()

        def domainObject = CircularSelf.build()
        assertNotNull domainObject
        assertEquals domainObject.circularSelf, domainObject
    }

    void testCascadingBuildCircularTwoClasses() {
        def domainArtefact = registerDomainClass(CircularOne)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(CircularTwo)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def circDomainOne = new DefaultGrailsDomainClass(CircularOne)
        def domainProp = circDomainOne.properties.find {it.name == 'circularTwo' }
        assertTrue domainProp.isOneToOne()

        def circDomainTwo = new DefaultGrailsDomainClass(CircularTwo)
        domainProp = circDomainTwo.properties.find {it.type == CircularOne }
        assertEquals domainProp.name, 'circularOne'
        def domainObject = CircularOne.build()
        assertNotNull domainObject
        assertEquals domainObject.circularTwo.circularOne, domainObject
    }

    void testEmbeddedProperties() {
        def domainArtefact = registerDomainClass(EmbeddedOwner)
        buildTestDataService.decorateWithMethods(domainArtefact)
        domainArtefact = registerDomainClass(Owned)
        buildTestDataService.decorateWithMethods(domainArtefact)

        def domainObject = EmbeddedOwner.build('owned.testProperty':'I am embedded')
        assertNotNull domainObject
        assertNotNull domainObject.testProperty
        assertNotNull domainObject.owned.testProperty
        assertEquals domainObject.owned.testProperty, 'I am embedded'

        shouldFail{ 
		    EmbeddedOwner.build('tranProp':3)   //setting readonly property
			 }
    }





}

class Owner {
    Long id
    Long version
    String testProperty
    Owned owned
}

class EmbeddedOwner {
   Long id
   Long version
   String testProperty
   int getTranProp() { 5 }

   static embedded = [ 'owned']
   static transients = [ 'tranProp']
   static constraints = {
			tranProp(nullable:true)
	}
	
   Owned owned = new Owned()

}


class Owned {
    Long id
    Long version
    String testProperty
}

class CircularSelf {
    Long id
    Long version
    CircularSelf circularSelf
    static constraints = {
        circularSelf(nullable: false)
    }
}

class CircularOne {
    Long id
    Long version
    CircularTwo circularTwo
}

class CircularTwo {
    Long id
    Long version
    CircularOne circularOne
}




