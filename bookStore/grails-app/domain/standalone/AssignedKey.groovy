package standalone

/**
 * Test domain object with assigned key
 */
class AssignedKey {
    String id
    String attribute

    static constraints = {
        id(blank: false)
    }

    static mapping = {
        id(generator: 'assigned')
    }
}
