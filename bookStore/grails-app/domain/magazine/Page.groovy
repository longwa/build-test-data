package magazine

class Page implements Comparable {
    Integer number
    static hasMany = [advertisments: Advertisment]
    static belongsTo = [issue: Issue]

    public int compareTo(Object o) {
        return number <=> o.number
    }
}

