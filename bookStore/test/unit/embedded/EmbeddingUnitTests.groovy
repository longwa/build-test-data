package embedded

import grails.buildtestdata.mixin.Build

@Build(Embedding)
class EmbeddingUnitTests {

    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
