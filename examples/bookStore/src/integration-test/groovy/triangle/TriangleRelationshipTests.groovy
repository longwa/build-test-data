package triangle

import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class TriangleRelationshipTests extends Specification implements TestDataBuilder {
    void testBuildTriangleRelationship() {
        // Director has many managers and workers
        // manager belongsto director and has many workers
        // workers belongs to a director and a manager, we should be able to build any one of these successfully
        expect:
        build(Worker)
        build(Manager)
        build(Director)
    }

    void testBuildTriangleRelationshipPartiallyCompleteAlready() {
        when:
        def manager = build(Manager)

        then:
        manager

        when:
        def director = build(Director, [managers: [manager]])
        then:
        director
        build(Worker, [manager: manager])
        build(Worker, [director: director])

    }
}
