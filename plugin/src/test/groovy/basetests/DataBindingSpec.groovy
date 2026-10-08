package basetests

import grails.buildtestdata.BuildDomainTest
import grails.buildtestdata.TestData
import grails.buildtestdata.builders.PogoDataBuilder
import spock.lang.Specification

class DataBindingSpec extends Specification implements BuildDomainTest<DataBindingDom> {

    void "values are converted to the property type"() {
        when:
        def domainObject = DataBindingDom.build(age: '42', count: 5, status: 'ACTIVE', amount: '1.5')

        then:
        domainObject.age == 42
        domainObject.count == 5L
        domainObject.status == DataBindingStatus.ACTIVE
        domainObject.amount == 1.5G
    }

    void "a value that can't be converted to the property type fails the build"() {
        when:
        DataBindingDom.build(save: save, (property): value)

        then:
        IllegalArgumentException e = thrown()
        e.message.contains("basetests.DataBindingDom.${property}")
        e.message.contains("'${value}'")

        where:
        property | value                  | save
        'age'    | 'abc'                  | true
        'age'    | 'abc'                  | false
        'count'  | 'abc'                  | false
        'status' | 'BOGUS'                | true
        'owner'  | 'not an owner'         | true
        'amount' | 'xyz'                  | true
    }

    void "a value that can't be converted for a nested object fails the build"() {
        when:
        DataBindingDom.build(owner: [rank: 'abc'])

        then:
        IllegalArgumentException e = thrown()
        e.message.contains('basetests.DataBindingOwner.rank')
    }

    void "every value that can't be converted is reported"() {
        when:
        DataBindingDom.build(age: 'abc', status: 'BOGUS')

        then:
        IllegalArgumentException e = thrown()
        e.message.contains('basetests.DataBindingDom.age')
        e.message.contains('basetests.DataBindingDom.status')
    }

    void "a value that can't be converted for a POGO fails the build"() {
        when:
        TestData.build(DataBindingPogo, [count: 'abc'])

        then:
        IllegalArgumentException e = thrown()
        e.message.contains('basetests.DataBindingPogo.count')
    }

    void "a value for a property that doesn't exist is ignored"() {
        when:
        def domainObject = DataBindingDom.build(nmae: 'typo', name: 'real')

        then:
        domainObject.name == 'real'
        !domainObject.hasProperty('nmae')
    }

    void "values for properties that don't exist are found so they can be logged"() {
        given:
        PogoDataBuilder builder = (PogoDataBuilder) TestData.findBuilder(DataBindingDom)

        expect:
        builder.findUnknownProperties(new DataBindingDom(), [nmae: 'typo', name: 'real', 'tags[0]': 'tag']) == ['nmae'] as Set
    }
}

enum DataBindingStatus { ACTIVE, INACTIVE }

@grails.persistence.Entity
class DataBindingOwner {
    String title
    Integer rank
    static constraints = {
        rank nullable: true
    }
}

@grails.persistence.Entity
class DataBindingDom {
    String name
    Integer age
    Long count
    DataBindingStatus status
    DataBindingOwner owner
    BigDecimal amount
    List<String> tags
    static hasMany = [tags: String]
    static constraints = {
        age nullable: true
        status nullable: true
        owner nullable: true
        amount nullable: true
        tags nullable: true
    }
}

class DataBindingPogo {
    Integer count
}
