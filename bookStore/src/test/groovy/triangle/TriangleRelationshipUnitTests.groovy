package triangle

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
@Build([Worker, Manager, Director])
class TriangleRelationshipUnitTests {
    @Test
    void testBuildTriangleRelationship() {
		// director has many managers and workers
		// manager belongsto director and has many workers
		// workers belongs to a director and a manager, we should be able to build any one of these successfully
		assert Worker.build()
		assert Manager.build()
		assert Director.build()
    }

	void testBuildTriangleRelationshipPartiallyCompleteAlready() {
		def manager = Manager.build()
		assert manager
		
		def director = Director.build(managers: [manager])
		assert director
		assert Worker.build(manager: manager)
		assert Worker.build(director: director)
	}
}
