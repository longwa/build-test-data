package embedded

import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class EmbeddingTests extends Specification implements TestDataBuilder {
    void "default embedded value"() {
        TestDataConfigurationHolder.sampleData = [:]

        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue == 'someValue'
    }

    void "embedded value loaded from TestDataConfig"() {
        TestDataConfigurationHolder.reset()

        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue == 'value'
    }

    void "embedded value overriden using the nested map syntax"() {
        when:
        Embedding e = build(Embedding, [inner: [someValue: 'test']])

        then:
        e.inner.someValue == 'test'
    }

    void "embedded value overriden via object parameter"() {
        when:
        Embedding e = build(Embedding, [inner: new Embedded(someValue: 'test')])

        then:
        e.inner.someValue == 'test'
    }

    void cleanupSpec() {
        TestDataConfigurationHolder.reset()
    }
}
