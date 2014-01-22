package embedded

import grails.buildtestdata.mixin.Build
import org.junit.Test

@Build(Embedding)
class EmbeddingUnitTests {

    @Test
    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
