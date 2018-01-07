package bookstore

import embedded.LatLon

class Address {
    String address1
    String address2
    String city
    String state
    String zip
    String emailAddress
    String webSite

    LatLon latLon
    LatLon altLatLon

    static embedded = ['latLon', 'altLatLon']

    static constraints = {
        address1(maxSize: 55)
        address2(maxSize: 55, nullable: true)
        city(maxSize: 30)
        state(maxSize: 30)
        zip(matches: /\d{5}/, nullable: true)
        emailAddress(email: true, minSize: 40)
        webSite(url: true, minSize: 40)
        latLon(nullable: true)
        altLatLon(nullable: true)
    }
}

