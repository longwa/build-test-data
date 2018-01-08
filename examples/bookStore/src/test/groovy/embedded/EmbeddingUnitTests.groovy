package embedded

import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification

class EmbeddingUnitTests extends Specification implements UnitTestDataBuilder {
    @Override
    Class[] getDomainClassesToMock() {
        [Embedding]
    }

    void testPlugin() {
        when:
        Embedding e = build(Embedding)
        then:
        e.inner.someValue != null
    }
}
