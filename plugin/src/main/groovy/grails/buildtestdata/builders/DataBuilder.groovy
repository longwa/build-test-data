package grails.buildtestdata.builders

interface DataBuilder {
    def build(DataBuilderContext ctx)
    def buildLazy(DataBuilderContext ctx)
    def buildWithoutSave(DataBuilderContext ctx)
}