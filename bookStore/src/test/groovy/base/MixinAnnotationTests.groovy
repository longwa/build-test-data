package base

import bookstore.Author
import grails.buildtestdata.mixin.BuildTestDataUnitTestMixin
import grails.test.mixin.TestMixin

@TestMixin(BuildTestDataUnitTestMixin)
class MixinAnnotationTests {

    void testMockForBuildAddsBuildMethod() {
        assert !Author.metaClass.hasMetaMethod('build')
        mockForBuild([Author])
        assert Author.metaClass.hasMetaMethod('build')

        def domainObject = Author.build()
        assert domainObject
    }
}
