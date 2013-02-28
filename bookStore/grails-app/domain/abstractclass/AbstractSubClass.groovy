package abstractclass

// Another abstract class in the hierarchy, just to make things interesting
abstract class AbstractSubClass extends AbstractClass {
    String subClassAttribute

    String toString() {
        "AbstractSubClass: $subClassAttribute"
    }
}
