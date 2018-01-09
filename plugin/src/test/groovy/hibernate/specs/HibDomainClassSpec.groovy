package hibernate.specs

import grails.buildtestdata.TestDataBuilder
import grails.test.hibernate.HibernateSpec
import hibernate.domains.Bar
import hibernate.domains.Foo

//don't use trait on this one, use the TestDataBuilder
class HibDomainClassSpec extends HibernateSpec implements TestDataBuilder {

    //dont' need to give it Bar as it will see its an association and mock it
    List<Class> getDomainClasses() {[ Foo ]}

    void setupSpec(){
        //build a Bar here to test buildLazy
        Bar.withTransaction {
            build(Bar)
        }
    }

    void "test that Bar got mocked and saved"() {

        when: "buildLazy should pick up the one thats already there from setupSpec"
        def bar = buildLazy(Bar) //should pick up the one thats already there

        then: "the id should be 1 as it was saved already"
        bar.id == 1
        bar.name
    }

    void "test foo and its bar association"() {
        build(Foo, [name: 'bill'])
        def foo = Foo.findById(1) //this is the first insert of Foo so 1 should be there

        expect:
        foo.id == 1
        foo.name == 'bill'
        foo.bar.name == 'name'
        foo.bar.id == 2
        println foo.properties

    }

    void "round 2"() {
        when: "buildLazy is called but will create a new one"
        def foo = buildLazy(Foo)
        //build(Foo)

        then: "a new Foo was saved since the build was called in another test and not in setupSpec"
        foo.id == 2
        foo.bar.id == 3
        println foo.properties

    }
}

