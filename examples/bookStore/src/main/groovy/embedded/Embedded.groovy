package embedded

import grails.validation.Validateable

class Embedded implements Validateable {
    String someValue

    static constraints = {
        someValue nullable: false
    }
}
