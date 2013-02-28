package base

import grails.test.*
import config.Article
import grails.buildtestdata.TestDataConfigurationHolder

class ConfigWithoutResetTests extends GroovyTestCase {

    // the TestDataConfig file sets the name values for Article's unique name property
    // it has a variable in the config that increments if we don't reset() the config

    void testBuildFirstUniqueArticle() {
        TestDataConfigurationHolder.reset() // reset to a known state for these tests

        def a = Article.build()
        assertEquals "Article 1", a.name
    }

    void testBuildSecondUniqueArticle() {
        // haven't reset between tests so we should get Article 2, the tests are interdependent
        // this isn't necessarily a suggested practice, but should still work
        def a = Article.build()
        assertEquals "Article 2", a.name
    }
}
