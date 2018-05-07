package base

import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import standalone.NonBindable

@Rollback
@Integration
class NonBindableTests extends Specification implements TestDataBuilder {
    void "non-bindable required property is not set by default"() {
        when:
        def standalone = build([save: false], NonBindable)

        then:
        standalone
        standalone.name == "name"

        and: "non-bindable property is not set"
        standalone.uuid == null
    }

    void "non-bindable required property can be set manually on build"() {
        when:
        def standalone = build([save: false], NonBindable, [uuid: 'abcd'])

        then:
        standalone
        standalone.name == "name"

        and: "non-bindable property is not set"
        standalone.uuid == 'abcd'
    }

    void "non-bindable required property can be set in TDCH"() {
        TestDataConfigurationHolder.sampleData = [('standalone.NonBindable'): [uuid: 'xxxx']]

        when:
        def standalone = build([save: false], NonBindable)

        then:
        standalone
        standalone.name == "name"

        and: "non-bindable property is not set"
        standalone.uuid == 'xxxx'
    }

    void cleanupSpec() {
        TestDataConfigurationHolder.reset()
    }
}
