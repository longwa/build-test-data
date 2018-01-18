package hibernate.specs

import grails.test.hibernate.HibernateSpec
import static grails.buildtestdata.TestData.build

class HibernateSpecSpec extends HibernateSpec {

    //HibernateSpec scans the package the test sits in. It will automatically find the Baz
    void "test hibernate spec with automatic scan"() {
        when:
        build(Baz)

        then:
        def baz1 = Baz.findById(1)
        baz1.name
        baz1.nullName == null
        //baz1.nameWithExample == 'shibbeldy'
    }
}

@grails.persistence.Entity
class Baz {
    String name
    String nullName
    String nameWithExample

    static constraints = {
        nullName nullable: true
        //nameWithExample example: 'shibbeldy'
    }
}
