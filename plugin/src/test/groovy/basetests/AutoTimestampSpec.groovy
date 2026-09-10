package basetests

import spock.lang.Specification
import spock.lang.Unroll

/**
 * GORM never applies a nullable constraint to its auto-timestamp properties (dateCreated and lastUpdated), but it
 * still reports them as not nullable. GORM only fills them in when the insert actually happens, which in the unit
 * datastore (and in Hibernate with a sequence generator) is at flush time, so the builder has to set them itself.
 *
 * Other properties GORM reports the same way, such as an identifier mentioned in the constraints block, must be left
 * alone: a generated identifier makes separately built instances overwrite each other.
 */
class AutoTimestampSpec extends Specification implements DomainTestBase {

    Class timestampedClass(String typeName, String suffix = '', String body = '') {
        createDomainClass("""
            @grails.persistence.Entity
            class TestTimestamp${typeName.tokenize('.').last()}$suffix {
                Long id
                Long version
                String name
                $typeName dateCreated
                $typeName lastUpdated
                $body
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

    void "an identifier mentioned in the constraints block is left for the datastore to assign"() {
        given: 'id bindable: true puts id in the constraints map with no applied constraints, just like the timestamps'
        Class domainClass = timestampedClass('java.time.LocalDateTime', 'BindableId', '''
            static constraints = {
                id bindable: true
            }
        ''')

        expect:
        domainClass.constrainedProperties.containsKey('id')
        !domainClass.constrainedProperties.id.nullable
        domainClass.constrainedProperties.id.appliedConstraints.empty

        when:
        def unsaved = domainClass.build(save: false)

        then:
        unsaved.id == null
        unsaved.dateCreated != null

        when: 'two instances are built and saved'
        def first = domainClass.build()
        def second = domainClass.build()

        then: 'they get their own identifiers instead of overwriting each other'
        first.id != null
        second.id != null
        first.id != second.id
        domainClass.count() == 2
    }

    void "properties annotated with @AutoTimestamp are populated too"() {
        given:
        Class domainClass = createDomainClass("""
            import grails.gorm.annotation.AutoTimestamp

            @grails.persistence.Entity
            class TestAnnotatedTimestamp {
                Long id
                Long version
                String name
                @AutoTimestamp
                java.time.LocalDateTime created
                @AutoTimestamp(AutoTimestamp.EventType.UPDATED)
                java.time.LocalDateTime modified
            }
        """)

        when:
        def domainObject = domainClass.build(save: false)

        then:
        domainObject.created != null
        domainObject.modified != null
    }

    void "timestamps are left alone when the entity is mapped with autoTimestamp false"() {
        given:
        Class domainClass = timestampedClass('java.time.LocalDateTime', 'NoAuto', '''
            static mapping = {
                autoTimestamp false
            }
        ''')

        when:
        def domainObject = domainClass.build(save: false)

        then:
        domainObject.dateCreated == null
        domainObject.lastUpdated == null
    }
}
