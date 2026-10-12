package base

import beanvalidation.Membership
import grails.buildtestdata.TestDataBuilder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
@Rollback
class BeanValidationTests extends Specification implements TestDataBuilder {

    void "a domain class constrained with Bean Validation annotations is built and saved"() {
        when:
        Membership membership = build(Membership)

        then:
        membership.id
        membership.validate()
        membership.memberName.trim()
        membership.memberName.size() <= 20
        membership.email.contains('@')
        membership.level in 1..5
        membership.fee > 0
        membership.joined < new Date()
        Membership.count() == 1
    }
}
