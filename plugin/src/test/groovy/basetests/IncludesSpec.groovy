package basetests

import grails.buildtestdata.TestData
import grails.buildtestdata.BuildDomainTest
import grails.buildtestdata.builders.DataBuilderContext
import spock.lang.Specification

class IncludesSpec extends Specification implements BuildDomainTest<IncludeDom> {

    void "sanity check optional is null"() {
        expect:
        domain.mustHave
        domain.optional == null
    }

    void "test include fields will throw exception"() {
        when:
        def ent = IncludeDom.build(includes: ['optional', 'optExt'])

        then: "its not mocked automatically since its not required"
        thrown RuntimeException
    }

    void "test getRequiredFields has includes fields"() {
        when:
        mockDomains(IncludeDomExt)
        def ent = IncludeDom.build(includes: ['optional', 'optExt'])
        DataBuilderContext ctx = new DataBuilderContext()
        ctx.includes = ['optional', 'optExt']
        Set incl = TestData.findBuilder(IncludeDom).getFieldsToBuild(ctx)

        then:
        incl.containsAll(['mustHave','optional', 'optExt'])
    }

    void "test getRequiredFields has all when include is '*"() {
        when:
        DataBuilderContext ctx = new DataBuilderContext()
        ctx.includes = '*'
        Set incl = TestData.findBuilder(IncludeDom).getFieldsToBuild(ctx)

        then:
        incl.containsAll(['mustHave','optional', 'optExt', 'amount'])
    }

    void "test include list works"() {
        when:
        def ent = IncludeDom.build(includes: ['optional'])

        then:
        ent.mustHave
        ent.optional
        ent.optExt == null
    }

    void "test include fields works with '*' for include all"() {
        when:
        def ent = IncludeDom.build(includes: '*')

        then:
         ent.mustHave
         ent.optional
         ent.optExt.title
    }

}

@grails.persistence.Entity
class IncludeDom {
    BigDecimal amount
    String mustHave
    String optional
    IncludeDomExt optExt
    static constraints = {
        optional nullable: true
        optExt nullable: true
        amount nullable: true
    }
}

@grails.persistence.Entity
class IncludeDomExt {
    String title
    static belongsTo = [inc: IncludeDom]
}
