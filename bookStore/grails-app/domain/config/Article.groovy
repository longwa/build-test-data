package config

class Article {
    String name
    static constraints = {
        name(unique: true)
    }
}
