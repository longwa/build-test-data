package base

import grails.test.*
import config.Article
import grails.buildtestdata.TestDataConfigurationHolder

class ConfigWithoutResetTests extends GroovyTestCase {

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
        assertEquals "Article 1", a.name

        def b = Article.build()
        assertEquals "Article 2", b.name
    }
}
