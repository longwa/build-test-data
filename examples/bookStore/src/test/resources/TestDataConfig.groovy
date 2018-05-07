import abstractclass.ConcreteSubClass
import bookstore.Author
import config.Article
import embedded.Embedded

testDataConfig {

    // For unit tests, this indicates an implicit @Build relationship. In this case, anytime a Hotel is used in @Build
    // we also want to include Article and Author. This is useful if you define defaults in sampleData that explicitly
    // build other objects.
    unitAdditionalBuild = [
        'config.Hotel': [Article],
        'config.Article': [Author]
    ]

    // For polymorphic associations, this allows you to default the concrete class that is built automatically.
    // By default, BTD will find all concrete subclasses and build the first one alphabetically by name.
    abstractDefault = [
        'abstractclass.AbstractClass': ConcreteSubClass,
        'abstractclass.AbstractSubClass': ConcreteSubClass
    ]

    sampleData {
        // Hotel class is in "config" package so we use a string in the builder
        'config.Hotel' {
            // returns "Motel 6" for the name property whenever a Hotel is constructed
            // and a name is not already given
            name = "Motel 6"

            // returns a unique fax number at each request
            def i = 6125551111
            faxNumber = {-> "${i++}" } // creates "6125551111", "6125551112", .... 
        }
        'config.Article' {
            def i = 1
            name = {-> "Article ${i++}" }
        }
        'bookstore.EstablishedAuthor' {
            metaData = ['tags': ['fiction', 'horror']]
        }

        // work around for embedded objects in src/groovy
        // grails does not create Artefacts that we can query for constraints for things outside of grails-app/domain
        'embedded.Embedding' {
            inner = {-> new Embedded(someValue: "value") }
        }

        'standalone.ParentWithAssignedKey' {
            def i = 1
            id = {-> "pid${i++}" }
        }
        'standalone.ChildWithAssignedKey' {
            def i = 1
            id = {-> "cid${i++}" }
        }
    }
}

