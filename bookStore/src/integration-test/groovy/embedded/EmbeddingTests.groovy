package embedded

import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class EmbeddingTests extends Specification implements TestDataBuilder {
    void testPlugin() {
        when:
        Embedding e = build(Embedding)

        then:
        e.inner.someValue != null
    }
}
