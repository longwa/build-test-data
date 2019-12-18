package assigned

class AssignedKeyComplex {

    Long id
    String attribute

    AssignedKeyBelongs keyBelongs
    AssignedKeyHas keyHas

    static constraints = {
        id(nullable: false, unique: true, bindable: true)
    }

    static mapping = {
        id(generator: 'assigned')
    }
}
