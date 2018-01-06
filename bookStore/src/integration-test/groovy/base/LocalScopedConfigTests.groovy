package base

import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import magazine.Issue
import magazine.Page
import spock.lang.Specification

@Rollback
@Integration
class LocalScopedConfigTests extends Specification implements TestDataBuilder {
    void testAddPagesToIssue() {
        when:
        def issue = build(Issue)
        issue.pages = (1..5).collect { build(Page, [issue: issue, number: it]) }.toSet() as SortedSet

        then:
        issue.pages.size() == 5
        Issue.list().size() // don't build extra issues when building pages == 1
    }
}
