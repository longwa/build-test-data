package hibernate4

class Gallery {
    String name
    static hasMany = [paintings: Painting]
}
