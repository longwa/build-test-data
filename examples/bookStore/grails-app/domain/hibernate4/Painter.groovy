package hibernate4

class Painter {
    String name

    static hasMany = [paintings: Painting]
}
