package subclassing

class RelatedClass {

    String name

    static hasMany = [superClassInstances: SuperClass]

    String toString() { "RelatedClass: $name" }
}
