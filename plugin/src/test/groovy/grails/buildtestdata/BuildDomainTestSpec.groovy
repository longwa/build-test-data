package grails.buildtestdata

import spock.lang.Shared
import spock.lang.Specification

class BuildDomainTestSpec extends Specification implements BuildDomainTest<SampleUnitTestDomain>{
    @Shared int id

    void "test basic persistence mocking"() {
        setup:
        build().save()
        build().save()

        expect:
        SampleUnitTestDomain.count() == 2
        SampleUnitTestDomainChild.count() == 2
    }

    void "test domain instance"() {
        setup:
        id = System.identityHashCode(domain)

        expect:
        domain != null
        domain.hashCode() == id
        domain.name != null

        when:
        domain.name = 'Robert'

        then:
        domain.name == 'Robert'
    }

    void "test props"() {
        when:
        def u = build(name: 'bill')
        entity.name = 'bob'

        then:
        u.name == 'bill'
        entity.name == 'bob'
    }

    void "test we get a new domain each time"() {
        expect:
        entity != null
        entity.name == 'name'
        entity.child
        System.identityHashCode(entity) != id
    }

}

@grails.persistence.Entity
class SampleUnitTestDomain {
    String name
    SampleUnitTestDomainChild child
}

@grails.persistence.Entity
class SampleUnitTestDomainChild {
    String name
}
