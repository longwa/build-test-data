package basetests

import spock.lang.Specification
import spock.lang.Unroll

/**
 * GORM never applies a nullable constraint to its auto-timestamp properties (dateCreated and lastUpdated), but it
 * still reports them as not nullable. GORM only fills them in when the insert actually happens, which in the unit
 * datastore (and in Hibernate with a sequence generator) is at flush time, so the builder has to set them itself.
 */
class AutoTimestampSpec extends Specification implements DomainTestBase {

    Class timestampedClass(String typeName, String suffix = '') {
        createDomainClass("""
            @grails.persistence.Entity
            class TestTimestamp${typeName.tokenize('.').last()}$suffix {
                Long id
                Long version
                String name
                $typeName dateCreated
                $typeName lastUpdated
            }
        """)
    }

    @Unroll
    void "dateCreated and lastUpdated of type #typeName are populated when built without saving"(String typeName) {
        given:
        Class domainClass = timestampedClass(typeName)

        when:
        def domainObject = domainClass.build(save: false)

        then: 'GORM treats the timestamps as required but gives the handlers no constraints to act on'
        !domainClass.constrainedProperties.dateCreated.nullable
        domainClass.constrainedProperties.dateCreated.appliedConstraints.empty

        and:
        domainObject.dateCreated != null
        domainObject.lastUpdated != null
        domainObject.dateCreated.class.name == typeName
        domainObject.lastUpdated.class.name == typeName

        where:
        typeName << ['java.time.LocalDateTime', 'java.time.Instant', 'java.util.Date']
    }

    void "dateCreated and lastUpdated are populated by build() before the session is flushed"() {
        given:
        Class domainClass = timestampedClass('java.time.LocalDateTime', 'Saved')

        when: 'saved but not flushed, so GORM has not run its own auto-timestamping yet'
        def unflushed = domainClass.build()

        then:
        unflushed.id != null
        unflushed.dateCreated != null
        unflushed.lastUpdated != null

        when:
        def flushed = domainClass.build(flush: true)

        then:
        flushed.dateCreated != null
        flushed.lastUpdated != null
    }
}
