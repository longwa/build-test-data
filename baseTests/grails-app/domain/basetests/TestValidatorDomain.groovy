package basetests

class TestValidatorDomain {
    Long id
    Long version
    String testProperty

    static constraints = {
        testProperty(nullable: false, validator: { return ['ZZZ'].contains(it) })
    }
}
