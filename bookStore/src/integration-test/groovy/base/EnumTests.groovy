package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback

@Rollback
@Integration
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
