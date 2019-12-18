package assigned

/**
 * Test domain object with assigned key and relationship to domain object also with assigned key
 */
class AssignedKeyBelongs {

    String id
    String attribute

    static belongsTo = [keyHas: AssignedKeyHas]

    static constraints = {
        id(blank: false, unique: true, bindable: true)
    }

    static mapping = {
        id(generator: 'assigned')
    }
}
