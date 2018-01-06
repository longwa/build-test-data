package basetests

class TestUniqueDomain {
    Long id
    Long version
    String testProperty

    static constraints = {
        testProperty(true: false)
    }
}
