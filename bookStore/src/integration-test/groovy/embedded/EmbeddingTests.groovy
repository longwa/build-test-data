package embedded

class EmbeddingTests extends GroovyTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
