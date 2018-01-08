package basetests

import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat
import java.time.LocalDate

class ExampleDataSpec extends Specification implements DomainTestBase {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    void setupSpec(){
        sdf.setTimeZone(TimeZone.getTimeZone('UTC'))
    }

    void "test uses example if its there"() {
        when:
        mockDomain(ExampleDataDom)
        ExampleDataDom entity = ExampleDataDom.build()

        then:
        entity.name == 'name'
        entity.nameNull == null
        entity.stringEx == 'Bill'
        entity.longEx == 99
        entity.bigdEx == 42.42
        sdf.format(entity.dateEx).substring(0,10) == sdf.format(new Date()).substring(0,10)
        entity.localDateEx.toString() == '2018-01-09'

        entity.bigdExFromStr == 42.42
        sdf.format(entity.dateExFromStr).startsWith('2013-11-01T23:00:00')
        entity.locDateFromStr.toString() == '2018-01-09'
    }

}

@grails.persistence.Entity
class ExampleDataDom {
    String name
    String nameNull
    String stringEx
    Long longEx
    Date dateEx
    LocalDate localDateEx
    BigDecimal bigdEx

    Date dateExFromStr
    LocalDate locDateFromStr
    BigDecimal bigdExFromStr

    static constraints = {
        nameNull    example: 'no go', nullable: true
        stringEx    example: 'Bill'
        longEx      example: 99
        bigdEx      example: 42.42
        dateEx      example: new Date()
        localDateEx example: LocalDate.parse('2018-01-09')

        bigdExFromStr  example: '42.42'
        dateExFromStr  example: '2013-11-01T23:00:00Z'
        locDateFromStr example: '2018-01-09'
    }
}