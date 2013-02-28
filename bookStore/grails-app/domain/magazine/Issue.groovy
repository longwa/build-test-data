package magazine

class Issue {
    String title
    SortedSet pages
    static hasMany = [pages: Page]
}

