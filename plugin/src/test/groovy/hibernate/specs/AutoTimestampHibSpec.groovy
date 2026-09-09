package hibernate.specs

import grails.test.hibernate.HibernateSpec

import java.time.LocalDateTime

import static grails.buildtestdata.TestData.build

/**
 * Hibernate auto-timestamps at insert time. With a sequence generator the insert waits for the flush, so
 * dateCreated and lastUpdated would stay null after build() unless the builder sets them itself.
 */
class AutoTimestampHibSpec extends HibernateSpec {

    void "timestamps are set by build() before the flush and survive the insert"() {
        when: 'the identity generator inserts immediately'
        def stamped = build(Stamped)

        then:
        stamped.dateCreated != null
        stamped.lastUpdated != null
        Stamped.findById(stamped.id).dateCreated == stamped.dateCreated

        when: 'the sequence generator defers the insert until the flush'
        def sequenced = build(SequenceStamped)

        then:
        sequenced.dateCreated != null
        sequenced.lastUpdated != null

        when:
        SequenceStamped.withSession { it.flush() }

        then:
        sequenced.dateCreated != null
        sequenced.lastUpdated != null
        SequenceStamped.findById(sequenced.id).lastUpdated != null
    }

    void "lastUpdated still advances on update"() {
        given:
        def stamped = build(Stamped, flush: true)
        LocalDateTime created = stamped.dateCreated
        LocalDateTime updated = stamped.lastUpdated

        when:
        stamped.name = 'changed'
        stamped.save(flush: true, failOnError: true)

        then:
        stamped.dateCreated == created
        stamped.lastUpdated != updated
    }
}

@grails.persistence.Entity
class Stamped {
    String name
    LocalDateTime dateCreated
    LocalDateTime lastUpdated
}

@grails.persistence.Entity
class SequenceStamped {
    String name
    LocalDateTime dateCreated
    LocalDateTime lastUpdated

    static mapping = {
        id generator: 'sequence'
    }
}
