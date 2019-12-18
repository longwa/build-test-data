package assigned

/**
 * Test domain object with assigned key and relationship to domain object also with assigned key
 */
class AssignedKeyHas {

    Long id
    String attribute

    AssignedKeyBelongs keyBelongs

    static constraints = {
        id(nullable: false, unique: true, bindable: true)
    }

    static mapping = {
        id(generator: 'assigned')
    }
}
