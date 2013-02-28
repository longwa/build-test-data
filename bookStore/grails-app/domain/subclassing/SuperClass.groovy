package subclassing

class SuperClass {
    String name
    static belongsTo = [relatedClass: RelatedClass]

    String toString() { "SuperClass: $name" }    
}
