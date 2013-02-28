class DomainTestDataServiceStringTests extends DomainTestDataServiceBase {
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testStringMinSize() {
        def minSize = 200
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(minSize: $minSize)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty.size() >= minSize
    }

    void testStringMaxSizeExceeded() {
        def maxSize = 2
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(maxSize: $maxSize)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty.size() <= maxSize
    }

    void testStringMaxSizeNotExceeded() {
        def maxSize = 200
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(maxSize: $maxSize)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty.size() <= maxSize
    }

    void testStringInList() {
        def firstInListItem = 'one'
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(inList: ['$firstInListItem', 'two'])
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty == firstInListItem
    }

    void testStringBlankFalse() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(blank: false)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
    }

    void testStringCreditCardNumber() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(creditCard: true)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
    }

    void testStringEmail() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(email: true)
                }
           }
        """)

		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
    }

    void testStringUrl() {
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(url: true)
                }
           }
        """)
		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
    }

    void testStringRange() {
        // appears to only check the first letter
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(range: 'x'..'z')
                }
           }
        """)
		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty == 'x'
     }

    void testStringSizeShrink() {
        def least = 1
        def most = 3
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(size: $least..$most)
                }
           }
        """)
		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty.size() == most
     }

    void testStringSizeExpand() {
        def least = 100
        def most = 300
        def domainClass = createDomainClass("""
            class TestDomain {
                Long id
                Long version
                String testProperty

                static constraints = {
                    testProperty(size: $least..$most)
                }
           }
        """)
		def domainObject = domainClass.build()

		assertNotNull domainObject
		assertNotNull domainObject.testProperty
		assertTrue domainObject.testProperty.size() == least
     }


}
