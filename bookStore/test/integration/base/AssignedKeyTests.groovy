package base

import standalone.AssignedKey

class AssignedKeyTests extends GroovyTestCase {
    void testBuildWithKey() {
        def obj = AssignedKey.build(id: "FOO")
        assert obj != null
        assert obj.id == "FOO"
        assert obj.attribute == "attribute"
    }

    void testBuildWithoutKey() {
        def obj = AssignedKey.build()
        assert obj != null
        assert obj.id == "id"
        assert obj.attribute == "attribute"
    }
}
