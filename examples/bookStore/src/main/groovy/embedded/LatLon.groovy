package embedded

class LatLon {
    BigDecimal longitude
    BigDecimal latitude

    static constraints = {
        longitude(min: -180.0, max: 180.0, scale: 4)
        latitude(min: -90.0, max: 90.0, scale: 4)
    }

    String toString() { "$longitude, $latitude" }
}