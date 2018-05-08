package base

import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.validation.Validateable
import spock.lang.Specification

class ValidateableBuilderSpec extends Specification implements TestDataBuilder {
    def "validateable instances can be built with default values"() {
        when:
        def obj = build(TestCommand)

        then:
        obj.name == "name."
        obj.age == 0
        !obj.isActive
    }

    def "validateable instances can be built with override values"() {
        when:
        def obj = build(TestCommand, [name: 'Aaron', isActive: true])

        then:
        obj.name == "Aaron"
        obj.age == 0
        obj.isActive
    }

    def "validateable instances can be built with sampleData"() {
        given:
        TestDataConfigurationHolder.sampleData = [('base.TestCommand'): [name: 'Bob']]

        when:
        def obj = build(TestCommand, [isActive: true])

        then:
        obj.name == "Bob"
        obj.age == 0
        obj.isActive
    }

    def "default validateable instances can be built with defaults"() {
        when:
        def obj = build(NullableTestCommand)

        then: "these are nullable by default now"
        obj.name == null
        obj.isActive == null

        and: "this is non null and should be set"
        obj.age == 0
    }

    void cleanupSpec() {
        TestDataConfigurationHolder.reset()
    }
}

@SuppressWarnings("GroovyUnusedDeclaration")
class TestCommand implements Validateable {
    String name
    Integer age
    Boolean isActive

    static constraints = {
        name(nullable: false, maxSize: 10, minSize: 5)
        age(nullable: false, min: 0)
    }
}

@SuppressWarnings("GroovyUnusedDeclaration")
class NullableTestCommand implements Validateable {
    String name
    Integer age
    Boolean isActive

    static constraints = {
        age(nullable: false, min: 0)
    }

    static boolean defaultNullable() {
        true
    }
}
