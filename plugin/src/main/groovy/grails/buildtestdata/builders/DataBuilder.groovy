package grails.buildtestdata.builders

interface DataBuilder {
    /**
     * builds the data using the passed in context
     * @param ctx the DataBuilderContext
     * @return the built entity.
     */
    def build(DataBuilderContext ctx)

    /**
     * builds the data using the passed in context
     *
     * @param args  optional argument map <br>
     *  - save        : (default: true) whether to call the save method when its a GormEntity <br>
     *  - find        : (default: false) whether to try and find the entity in the datastore first <br>
     *  - flush       : (default: false) passed in the args to the GormEntity save method <br>
     *  - failOnError : (default: true) passed in the args to the GormEntity save method <br>
     *  - include     : a list of the properties to build in addition to the required fields. use `*` to build all <br>
     * @param ctx the DataBuilderContext
     * @return the built entity.
     */
    def build(Map args, DataBuilderContext ctx)
}