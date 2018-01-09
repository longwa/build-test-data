package human
class Nose {
    String typeOfNose
    static constraints = {
        typeOfNose(inList: ['male', 'female'])
    }
}
