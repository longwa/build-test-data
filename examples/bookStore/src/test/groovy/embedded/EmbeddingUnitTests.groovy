package embedded

import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.TestDataConfigurationHolder
import spock.lang.Specification

class EmbeddingUnitTests extends Specification implements BuildDataUnitTest {
    @Override
    Class[] getDomainClassesToMock() {
        [Embedding]
    }

    void "embedded property with no default initial value"() {
        TestDataConfigurationHolder.sampleData = [:]

        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue == 'someValue'
    }

    void "embedded property with default initial value as object"() {
        TestDataConfigurationHolder.sampleData = [('embedded.Embedding'): [inner: new Embedded(someValue: 'test')]]

        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue == 'test'
    }

    void "embedded property with default initial value as map"() {
        TestDataConfigurationHolder.sampleData = [('embedded.Embedding'): [inner: [someValue: 'test']]]

        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue == 'test'
    }

    void "embedded property with global default for the embedded class"() {
        TestDataConfigurationHolder.sampleData = [('embedded.Embedded'): [someValue: 'globalDefault']]

        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue == 'globalDefault'
    }

    void cleanupSpec() {
        TestDataConfigurationHolder.reset()
    }
}
