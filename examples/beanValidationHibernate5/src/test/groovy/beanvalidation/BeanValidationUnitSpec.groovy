package beanvalidation

import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.mixin.Build
import spock.lang.Specification

/**
 * The unit datastore does not validate the annotations, but the same data is generated so unit tests are consistent
 * with integration tests
 */
@Build([Member, Subscription])
class BeanValidationUnitSpec extends Specification implements BuildDataUnitTest {

    void "the annotations are satisfied in the unit datastore too"() {
        when:
        Member member = Member.build()

        then:
        member.id
        member.name.trim()
        member.email.contains('@')
        member.level in 1..5
        member.balance > 0
        member.joined < new Date()
        member.referenceCode == null
        member.subscriptions.size() >= 1
    }
}
