package base

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback
import standalone.AssignedKey
import standalone.ChildWithAssignedKey

@TestMixin(IntegrationTestMixin)
@Rollback
class AssignedKeyTests {
    void testBuildWithKey() {
        AssignedKey.withSession { session ->
            AssignedKey.build(id: "FOO")
            session.flush()

            def obj = AssignedKey.get("FOO")
            assert obj != null
            assert obj.attribute == "attribute"
        }
    }

    void testBuildWithoutKey() {
        AssignedKey.withSession { session ->
            AssignedKey.build()
            session.flush()

            def obj = AssignedKey.get("id")
            assert obj != null
            assert obj.attribute == "attribute"
        }
    }

    void testBuildChildWithAssignedKeyInParent() {
        ChildWithAssignedKey.withSession { session ->
            ChildWithAssignedKey.build(id: 'FOO')
            session.flush()

            def obj = ChildWithAssignedKey.get("FOO")
            assert obj != null
            assert obj.attribute == "attribute"
        }
    }
}
