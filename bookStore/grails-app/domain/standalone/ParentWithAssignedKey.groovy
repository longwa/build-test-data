package standalone

/**
 * Test parent domain object with assigned key
 */
class ParentWithAssignedKey {
    String id

    static constraints = {
        id(blank: false)
    }

    static mapping = {
        autoTimestamp(true)
        id(generator: 'assigned')
        version(true)
    }
}
