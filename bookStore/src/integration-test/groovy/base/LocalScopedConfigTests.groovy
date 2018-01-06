package base

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import magazine.Issue
import magazine.Page
import org.junit.Test

@Rollback
@Integration
class LocalScopedConfigTests {

    @Test
    void testAddPagesToIssue() {
        def issue = Issue.build()
        issue.pages = (1..5).collect { Page.build(issue: issue, number: it) }.toSet() as SortedSet

        assert issue.pages.size() == 5
        assert Issue.list().size() // don't build extra issues when building pages == 1
    }
}
