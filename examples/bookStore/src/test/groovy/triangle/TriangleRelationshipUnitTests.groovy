package triangle

import grails.buildtestdata.BuildDataTest
import spock.lang.Specification

class TriangleRelationshipUnitTests extends Specification implements BuildDataTest {
    @Override
    Class[] getDomainClassesToMock() {
        [Worker, Manager, Director]
    }

    void testBuildTriangleRelationship() {
        // director has many managers and workers
        // manager belongsto director and has many workers
        // workers belongs to a director and a manager, we should be able to build any one of these successfully
        when:
        def w = build(Worker)
        then:
        w instanceof Worker
        w.manager
        w.director

        when:
        def m = build(Manager)
        then:
        m instanceof Manager
        m.director

        when:
        def d = build(Director)
        then:
        d instanceof Director
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

        when:
        def w = build(Worker, [manager: manager])
        then:
        w.manager

        when:
        w = build(Worker, [director: director])
        then:
        w.director
    }
}
