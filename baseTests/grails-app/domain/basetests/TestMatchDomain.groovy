package basetests

class TestMatchDomain {
    Long id
    Long version
    String testProperty

    static constraints = {
        testProperty(matches:"[A-Z]+")
    }
}
