package list

import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Specification

class SantaUnitTests extends Specification implements UnitTestDataBuilder {
    @Override
    Class[] getDomainClassesToMock() {
        [Santa, Child]
    }

    void testChildListOk() {
        when:
        def santa = build(Santa, [firstName: 'Santa', lastName: 'Claus'])
        then:
        santa.children == null

        when:
        build(Child, [name: 'ivan', grade: 2, dateOfBirth: new Date(), santa: santa])
        then:
        santa.children.size() == 1

        when:
        build(Child, [name: 'hazel', grade: 0, dateOfBirth: new Date(), santa: santa])

        then:
        santa.children.size() == 2
        santa.save(flush: true)
        santa.children.size() == 2
        santa.id > 0
    }

    void testElvesListMinConstraintOk() {
        when:
        def santa = build(Santa, [firstName: 'Santa', lastName: 'Claus'])

        then:
        santa.children == null
        santa.elves != null
        santa.elves.size() == 1
    }
}
