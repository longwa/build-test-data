package basetests

class TestNullableDomain {
    Long id
    Long version

    String testProperty
    String testNullableProperty
    String testDefaultProperty = 'default'

    static constraints = {
        testNullableProperty(nullable: true)
    }
}
