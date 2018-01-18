package basetests

import spock.lang.Specification

class StringTests extends Specification implements DomainTestBase {

    void testStringMinSize() {
        expect:
        def minSize = 200
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestStringMinSize {
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

    void testStringMaxSizeExceeded() {
        expect:
        def maxSize = 2
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestStringMaxSizeExceeded {
                Long id
                Long version
                String maxPropExceeds

                static constraints = {
                    maxPropExceeds(maxSize: $maxSize)
                }
           }
        """)

        def domainObject = domainClass.build()

        assert domainObject != null
        assert domainObject.maxPropExceeds != null
        assert domainObject.maxPropExceeds.size() <= maxSize
    }

    void testStringMaxSizeNotExceeded() {
        expect:
        def maxSize = 200
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestStringMaxSizeNotExceeded {
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

    void testStringInList() {
        expect:
        def firstInListItem = 'one'
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain2 {
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

    void testStringBlankFalse() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain3 {
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

    void testStringCreditCardNumber() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain4 {
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

    void testStringEmail() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain5 {
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

    void testStringUrl() {
        expect:
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain6 {
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

    void testStringRange() {
        expect:
        // appears to only check the first letter
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain7 {
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

    void testStringSizeShrink() {
        expect:
        def least = 1
        def most = 3
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain8 {
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


    void testStringSizeExpand() {
        expect:
        def least = 100
        def most = 300
        def domainClass = createDomainClass("""
            @grails.persistence.Entity
            class TestDomain9 {
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
