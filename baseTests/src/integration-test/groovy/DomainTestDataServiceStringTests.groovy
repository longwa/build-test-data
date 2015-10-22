import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import org.junit.Test

@TestMixin(IntegrationTestMixin)
class DomainTestDataServiceStringTests extends DomainTestDataServiceBase {
    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty.size() >= minSize
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty.size() <= maxSize
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty.size() <= maxSize
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty == firstInListItem
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
    }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty == 'x'
     }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty.size() == most
     }

    @Test
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

		assert domainObject != null
		assert domainObject.testProperty != null
		assert domainObject.testProperty.size() == least
     }
}
