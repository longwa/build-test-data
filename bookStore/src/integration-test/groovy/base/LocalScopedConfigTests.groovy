package base

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback
import magazine.Issue
import magazine.Page

@Rollback
@TestMixin(IntegrationTestMixin)
class LocalScopedConfigTests {
    void testAddPagesToIssue() {
        def issue = Issue.build()
        issue.pages = (1..5).collect { Page.build(issue: issue, number: it) }.toSet() as SortedSet

        assert issue.pages.size() == 5
        assert Issue.list().size() // don't build extra issues when building pages == 1
    }
}
