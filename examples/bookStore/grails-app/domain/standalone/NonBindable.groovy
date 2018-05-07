package standalone

class NonBindable {
    String uuid
    String name

    static constraints = {
        uuid(bindable: false)
    }
}
