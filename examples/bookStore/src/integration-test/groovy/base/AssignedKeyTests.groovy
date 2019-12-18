package base

import assigned.AssignedKeyComplex
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import assigned.AssignedKey
import assigned.AssignedKeyBelongs
import assigned.AssignedKeyHas
import assigned.ChildWithAssignedKey

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

        and: "key is assigned using blank handler"
        obj.id == 'x'

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

    void testBuildWithoutKeyInHasRelationship() {
        when:
        def obj = build(AssignedKeyHas)

        then:
        obj != null
        obj.attribute == "attribute"

        and: "key is assigned using nullable handler"
        obj.id == 0

        and: "assigned key association exists"
        obj.keyBelongs != null
        obj.keyBelongs.attribute == "attribute"

        and: "key is assigned using blank handler"
        obj.keyBelongs.id == 'x'

        and: "verify persistence"
        def has = AssignedKeyHas.get(obj.id)
        has != null
        has.keyBelongs != null
    }

    void testBuildWithoutKeyInBelongsRelationship() {
        when:
        def obj = build(AssignedKeyBelongs)

        then:
        obj != null
        obj.attribute == "attribute"

        and: "key is assigned using blank handler"
        obj.id == 'x'

        and: "assigned key association exists"
        obj.keyHas != null
        obj.keyHas.attribute == "attribute"

        and: "key is assigned using nullable handler"
        obj.keyHas.id == 0

        and: "verify persistence"
        def belongs = AssignedKeyBelongs.get(obj.id)
        belongs != null
        belongs.keyHas != null
    }

    void testBuildWithoutKeyInDeepRelationship() {
        when: "building an object graph that contains multiples of the same type using assigned String keys"
        def obj = build(AssignedKeyComplex)

        then:
        obj != null
        obj.keyHas != null
        obj.keyBelongs != null
        obj.keyHas.keyBelongs != null
    }
}
