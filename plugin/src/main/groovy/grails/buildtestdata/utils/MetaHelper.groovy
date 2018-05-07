package grails.buildtestdata.utils

import grails.buildtestdata.TestData

class MetaHelper {
    static void addBuildMetaMethods(Class<?>... entityClasses) {
        entityClasses.each { Class ec ->
            def mc = ec.metaClass
            mc.static.build = {->
                TestData.build(ec)
            }
            mc.static.build = { Map args ->
                TestData.build(args, ec)
            }
            mc.static.build = { Map args, Map data ->
                TestData.build(args, ec, data)
            }
            mc.static.findOrBuild = {->
                TestData.findOrBuild(ec, [:])
            }
            mc.static.findOrBuild = { Map data ->
                TestData.findOrBuild(ec, data)
            }
        }
    }
}
