package embedded


import org.junit.Test


class EmbeddingUnitTests {

    @Test
    void testPlugin() {
        Embedding e = Embedding.build()
        assert e.inner.someValue != null
    }
}
