package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import grails.transaction.Rollback

@Rollback
@TestMixin(IntegrationTestMixin)
class EnumTests {
    void testCarStatusEnumPopulated() {
        Car car = Car.build()
        assert car
        assert car.status == CarStatus.REVERSE
    }

    void testDoorStatusEnumPopulated() {
        Door door = Door.build()
        assert door
        assert door.status == DoorStatus.OPEN
    }
}
