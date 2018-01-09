package list

class Child {
    String name
    Date dateOfBirth
    Integer grade

    static belongsTo = [santa: Santa]
}
