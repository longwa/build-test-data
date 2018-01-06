package base

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Test
import standalone.AssignedKey
import standalone.ChildWithAssignedKey

@Integration
@Rollback
class AssignedKeyTests {

    @Test
    void testBuildWithKey() {
        AssignedKey.withSession { session ->
            AssignedKey.build(id: "FOO")
            session.flush()

            def obj = AssignedKey.get("FOO")
            assert obj != null
            assert obj.attribute == "attribute"
        }
    }

    @Test
    void testBuildWithoutKey() {
        AssignedKey.withSession { session ->
            AssignedKey.build()
            session.flush()

            def obj = AssignedKey.get("id")
            assert obj != null
            assert obj.attribute == "attribute"
        }
    }

    @Test
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
