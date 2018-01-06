package base

import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import standalone.AssignedKey
import standalone.ChildWithAssignedKey

@Integration
@Rollback
class AssignedKeyTests extends Specification implements TestDataBuilder {
    void testBuildWithKey() {
        when:
        def obj = build(AssignedKey, [id: "FOO"])

        then:
        obj != null
        obj.attribute == "attribute"

        def o2 = AssignedKey.get("FOO")
        o2 != null
    }

    void testBuildWithoutKey() {
        when:
        def obj = build(AssignedKey)

        then:
        obj != null
        obj.attribute == "attribute"

        def o2 = AssignedKey.get(obj.id)
        o2 != null
    }

    void testBuildChildWithAssignedKeyInParent() {
        when:
        def obj = build(ChildWithAssignedKey, [id: 'FOO'])

        then:
        obj != null
        obj.attribute == "attribute"

        def o2 = ChildWithAssignedKey.get('FOO')
        o2 != null
    }
}
