package list

class Santa {
    String firstName = "Santa"
    String lastName = "Claus"

    static hasMany = [children: Child, elves: Elf]

    static constraints = {
        elves(nullable: false, minSize: 1, validator: { val, obj ->
            val.every { it.validate() }
        })
    }
}