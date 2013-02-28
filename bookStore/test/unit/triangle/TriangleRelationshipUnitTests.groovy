package triangle

import grails.buildtestdata.mixin.Build
import org.junit.Test

@Build([Worker, Manager, Director])
class TriangleRelationshipUnitTests {

    @Test
    void testBuildTriangleRelationship() {
		// director has many managers and workers
		// manager belongsto director and has many workers
		// workers belongs to a director and a manager, we should be able to build any one of these successfully
		assertNotNull Worker.build()
		assertNotNull Manager.build()
		assertNotNull Director.build()
    }

	void testBuildTriangleRelationshipPartiallyCompleteAlready() {
		def manager = Manager.build()
		
		assertNotNull manager
		
		def director = Director.build(managers: [manager])
		
		assertNotNull director
		
		assertNotNull Worker.build(manager: manager)
		
		assertNotNull Worker.build(director: director)
		
	}
}
