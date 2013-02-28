package base

import grails.buildtestdata.mixin.Build
import magazine.Issue
import magazine.Page

@Build([Issue, Page])
class LocalScopedConfigUnitTests {
    void testAddPagesToIssue() {
        def issue = Issue.build()
        issue.pages = (1..5).collect { Page.build(issue: issue, number: it) }.toSet() as SortedSet
        
        assertEquals 5, issue.pages.size()
        assertEquals 1, Issue.list().size() // don't build extra issues when building pages
    }
}
