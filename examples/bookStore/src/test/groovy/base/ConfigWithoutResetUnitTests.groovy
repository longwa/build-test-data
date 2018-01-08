package base

import config.Article
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification

class ConfigWithoutResetUnitTests extends Specification implements UnitTestDataBuilder {
    void setup() {
        mockDomain(Article)
    }

    // the TestDataConfig file sets the name values for Article's unique name property
    // it has a variable in the config that increments if we don't reset() the config
    void testBuildFirstUniqueArticle() {
        TestDataConfigurationHolder.reset() // reset to a known state for these tests

        when:
        def a = build(Article)

        then:
        assert "Article 1" == a.name
    }

    void testBuildSecondUniqueArticle() {
        TestDataConfigurationHolder.reset()

        when:
        def a1 = build(Article)
        def a2 = build(Article)

        then:
        assert "Article 2" == a2.name
    }
}
