package bookstore
class Address {

    String address1
    String address2
    String city
    String state
    String zip
    String emailAddress
    String webSite
    LatLon latLon
    LatLon alternateLatLon

    static embedded = ['latLon', 'alternateLatLon']

    static constraints = {
        address1(maxSize:55)
        address2(maxSize:55, nullable:true)
        city(maxSize:30)
        state(maxSize:30)
        zip(matches:/\d{5}/, nullable:true)
        emailAddress(email:true, minSize:100)
        webSite(url: true, minSize:100)
        alternateLatLon(nullable: true)
    }

}
