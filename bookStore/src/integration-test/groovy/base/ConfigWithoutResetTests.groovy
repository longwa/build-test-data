package base

import config.Article
import grails.buildtestdata.TestDataConfigurationHolder
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback

@Rollback
@TestMixin(IntegrationTestMixin)
class ConfigWithoutResetTests {
    // test order isn't guaranteed with Java 7 but this way, with 2 tests, we know that reset is working because
    // one of the tests will run first, then the other, but both get the same articles
    void testBuildFirstUniqueArticle() {
        resetAndBuildUniqueArticles()
    }

    void testBuildSecondUniqueArticle() {
        resetAndBuildUniqueArticles()
    }

    private static resetAndBuildUniqueArticles() {
        TestDataConfigurationHolder.reset() // reset to a known state for these tests
        def a = Article.build()
        assert a.name == "Article 1"

        def b = Article.build()
        assert b.name == "Article 2"
    }
}
