package embedded

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Test

@Rollback
@Integration
class EmbeddingTests {

    @Test
    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
