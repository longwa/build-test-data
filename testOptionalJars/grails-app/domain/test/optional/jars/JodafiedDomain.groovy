package test.optional.jars

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.LocalDateTime
import org.joda.time.contrib.hibernate.PersistentLocalDate
import org.joda.time.contrib.hibernate.PersistentLocalTimeAsTime
import org.joda.time.contrib.hibernate.PersistentLocalDateTime
import org.joda.time.contrib.hibernate.PersistentDateTime

class JodafiedDomain {
    LocalDate localDate
    LocalTime localTime
    LocalDateTime localDateTime
    DateTime dateTime

    static mapping = {
        autoTimestamp true // populate dateCreated and lastUpdated automatically by grails framework
        localDate type: PersistentLocalDate
        localTime type: PersistentLocalTimeAsTime
        localDateTime type: PersistentLocalDateTime
        dateTime type: PersistentDateTime
        dateCreated type: PersistentDateTime
        lastUpdated type: PersistentDateTime
    }
}
