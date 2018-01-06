package triangle

import bookstore.Address
import bookstore.Foo
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Test

@Rollback
@Integration
class TriangleRelationshipTests {

    @Test
    void testBuildTriangleRelationship() {
        // Director has many managers and workers
        // manager belongsto director and has many workers
        // workers belongs to a director and a manager, we should be able to build any one of these successfully
        assert Worker.build()
        assert Manager.build()
        assert Director.build()
        assert Foo.build()
        assert Address.build()
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
