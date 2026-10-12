package base

import beanvalidation.Membership
import grails.buildtestdata.BuildDataUnitTest
import grails.buildtestdata.mixin.Build
import spock.lang.Specification

@Build(Membership)
class BeanValidationUnitSpec extends Specification implements BuildDataUnitTest {

    void "the Bean Validation annotations are satisfied in unit tests too"() {
        when:
        Membership membership = Membership.build()

        then:
        membership.id
        membership.memberName.trim()
        membership.email.contains('@')
        membership.level in 1..5
        membership.fee > 0
        membership.joined < new Date()
    }
}
