package beanvalidation

import grails.buildtestdata.TestDataBuilder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
@Rollback
class BeanValidationIntegrationSpec extends Specification implements TestDataBuilder {

    void "GORM validates the Bean Validation annotations"() {
        given:
        Member member = new Member()
        member.name = ' '
        member.email = 'not an email'
        member.level = 9
        member.balance = -1
        member.joined = new Date() + 1
        member.referenceCode = 'bad'
        member.subscriptions = [] as Set

        expect:
        !member.validate()
        ['name', 'email', 'level', 'balance', 'joined', 'referenceCode', 'subscriptions'].every { String field ->
            member.errors.getFieldError(field)
        }
        member.save() == null
    }

    void "build-test-data generates data satisfying the annotations"() {
        when:
        Member member = build(Member)

        then:
        member.id
        member.validate()
        member.name.trim()
        member.name.size() <= 30
        member.email.contains('@')
        member.level in 1..5
        member.balance > 0
        member.joined < new Date()
        member.subscriptions.size() >= 1
        member.subscriptions.every { Subscription subscription -> subscription.plan.trim() && subscription.member == member }

        and: "an optional property is left alone"
        member.referenceCode == null

        and: "everything is persisted"
        Member.count() == 1
        Subscription.count() == member.subscriptions.size()
    }

    void "an optional annotated property is generated when included"() {
        when:
        Member member = build([includes: ['referenceCode']], Member)

        then:
        member.referenceCode ==~ /[A-Z]{2}-\d{4}/
        member.validate()
    }

    void "overrides are kept and the rest is generated"() {
        when:
        Member member = build(Member, [name: 'Alice', level: 3])

        then:
        member.name == 'Alice'
        member.level == 3
        member.validate()
        member.id
    }
}
