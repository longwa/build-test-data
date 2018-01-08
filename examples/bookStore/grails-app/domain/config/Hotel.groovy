package config

// for use in exercising the grails-app/conf/TestDatConfig.groovy
class Hotel {
    String name
    String faxNumber
    static constraints = {
        name( blank:false, validator: { value, obj ->
            return ["Motel 6", "Super 8", "Holiday Inn", "Hilton", "Westin"].contains(value)
        })
    }
}
