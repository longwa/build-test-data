package mapping

class AutoTimestampDomain {
    Date dateCreated
    Date lastUpdated
    String name
    
    static mapping = {
        autoTimestamp true // populate dateCreated and lastUpdated automatically by grails framework
    }
}
