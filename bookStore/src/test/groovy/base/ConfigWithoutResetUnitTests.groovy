package base

import config.Article
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
@Build(Article)
class ConfigWithoutResetUnitTests {
    // the TestDataConfig file sets the name values for Article's unique name property
    // it has a variable in the config that increments if we don't reset() the config
    @Test
    void testBuildFirstUniqueArticle() {
        TestDataConfigurationHolder.reset() // reset to a known state for these tests

        def a = Article.build()
        assert "Article 1" == a.name
    }

    @Test
    void testBuildSecondUniqueArticle() {
        TestDataConfigurationHolder.reset()
        def a1 = Article.build()
        def a2 = Article.build()
        assert "Article 2" == a2.name
    }
}
