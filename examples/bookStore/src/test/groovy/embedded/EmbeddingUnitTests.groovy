package embedded

import grails.buildtestdata.BuildDataTest
import spock.lang.Specification

class EmbeddingUnitTests extends Specification implements BuildDataTest {
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
