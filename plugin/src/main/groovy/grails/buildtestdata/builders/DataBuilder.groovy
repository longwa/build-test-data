package grails.buildtestdata.builders

interface DataBuilder {
    def build(BuildTestDataContext ctx)
    def buildLazy(BuildTestDataContext ctx)
    def buildWithoutSave(BuildTestDataContext ctx)
}