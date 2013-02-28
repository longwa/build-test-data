package bookstore
class LatLon {
    BigDecimal longitude
    BigDecimal latitude

    static constraints = {
        longitude(min: -180.0, max: 180.0, scale: 14)
        latitude(min: -90.0, max: 90.0, scale: 14)
    }

    String toString() { "$longitude, $latitude" }
}
