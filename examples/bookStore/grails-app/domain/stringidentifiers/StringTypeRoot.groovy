package stringidentifiers

class StringTypeRoot {

    String id

    StringTypeSimple simple
    StringTypeComplex complex

    static constraints = {
        id blank: false
    }
}
