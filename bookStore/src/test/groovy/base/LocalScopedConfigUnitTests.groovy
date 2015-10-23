package base

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import magazine.Issue
import magazine.Page
import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
@Build([Issue, Page])
class LocalScopedConfigUnitTests {
    @Test
    void testAddPagesToIssue() {
        def issue = Issue.build()
        issue.pages = (1..5).collect { Page.build(issue: issue, number: it) }.toSet() as SortedSet
        
        assert 5 == issue.pages.size()
        assert 1 == Issue.list().size() // don't build extra issues when building pages
    }
}
