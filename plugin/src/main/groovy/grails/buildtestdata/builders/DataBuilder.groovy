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
     * @param args a map of option
     *  - save : (default: true) whether to call the save method when its a GormEntity
     *  - find : (default: false) whether to try and find the entity in the datastore first
     *  - flush : (default: false) passed in the args to the GormEntity save method
     *  - failOnError : (default: true) passed in the args to the GormEntity save method
     *  - include : a list of the properties to build in addition to the required fields.
     *  - includeAll : (default: false) build tests data for all fields in the domain
     * @param ctx the DataBuilderContext
     * @return the built entity.
     */
    def build(Map args, DataBuilderContext ctx)
}