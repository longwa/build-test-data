package embedded

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback

@Rollback
@Integration
class EmbeddingTests {
    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
