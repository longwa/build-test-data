package embedded

class Embedding {
    String name
    Embedded inner

    // grails unfortunately doesn't create domain artefacts for embedded objects created outside of grails-app/domain
    // for those, you'll need to define an explicit embedded object example in TestDataConfig to get it populated
    static embedded = ['inner']
}
