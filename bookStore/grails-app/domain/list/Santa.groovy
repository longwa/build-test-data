package list

class Santa {
    String firstName = "Santa"
    String lastName = "Claus"

    List children

    static hasMany = [children: Child]

    static constraints = {
    }
}
