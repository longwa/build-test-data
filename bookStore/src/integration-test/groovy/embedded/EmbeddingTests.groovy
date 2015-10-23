package embedded

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback

@Rollback
@TestMixin(IntegrationTestMixin)
class EmbeddingTests {
    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
