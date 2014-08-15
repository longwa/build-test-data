package base

import config.Hotel
import grails.buildtestdata.mixin.Build
import spock.lang.Ignore
import spock.lang.Specification

@Ignore  // this seems to be broken in 2.4.3, opened issue: https://jira.grails.org/browse/GRAILS-11661
@Build(Hotel)
class ExtendMetaClassSpec extends Specification {

    void setup() {
        assert Hotel.metaClass.hasMetaMethod("build")
    }

    void "metaClass build add baz but shouldn't have qux"() {
        given:
        assert !Hotel.metaClass.hasMetaMethod("qux")
        Hotel.metaClass."static".baz = {-> return "baz" }

        when:
        def hotel = Hotel.build()

        then:
        assertNotNull hotel
        assertEquals "baz", Hotel.baz()
    }

    void "metaClass build add qux but shouldn't have baz"() {
        given:
        assert !Hotel.metaClass.hasMetaMethod("baz")
        Hotel.metaClass."static".qux = {-> return "qux" }

        when:
        def hotel = Hotel.build()

        then:
        assertNotNull hotel
        assertEquals "qux", Hotel.qux()
    }
}
