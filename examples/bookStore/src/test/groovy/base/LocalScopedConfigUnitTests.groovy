package base

import grails.buildtestdata.BuildDataUnitTest
import magazine.Issue
import magazine.Page
import spock.lang.Specification

class LocalScopedConfigUnitTests extends Specification implements BuildDataUnitTest {
    void setupSpec() {
        mockDomains(Issue, Page)
    }

    void testAddPagesToIssue() {
        when:
        def issue = build(Issue)
        issue.pages = (1..5).collect { build(Page, [issue: issue, number: it]) }.toSet() as SortedSet

        then:
        assert 5 == issue.pages.size()
        assert 1 == Issue.list().size() // don't build extra issues when building pages
    }
}
