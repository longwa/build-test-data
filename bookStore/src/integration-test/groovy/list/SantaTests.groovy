package list

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Ignore
import org.junit.Test

@Rollback
@Integration
class SantaTests {

    @Test
    void testChildListOk() {
        def santa = Santa.build(firstName: 'Santa', lastName: 'Claus')
        assert santa.children == null

        Child.build(name: 'ivan', grade: 2, dateOfBirth: new Date(), santa: santa)
        assert santa.children.size() == 1

        Child.build(name: 'hazel', grade: 0, dateOfBirth: new Date(), santa: santa)
        assert santa.children.size() == 2

        santa.save(flush: true)
        assert santa.children.size() == 2
        assert santa.id > 0
    }

    @Test
    @Ignore
    void testElvesListMinConstraintOk() {
        def santa = Santa.build(firstName: 'Santa', lastName: 'Claus')
        assert santa.children == null
        assert santa.elves != null
        assert santa.elves.size() == 1
    }
}
