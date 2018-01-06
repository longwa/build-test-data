package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.junit.Test

@Rollback
@Integration
class EnumTests {

    @Test
    void testCarStatusEnumPopulated() {
        Car car = Car.build()
        assert car
        assert car.status == CarStatus.REVERSE
    }

    @Test
    void testDoorStatusEnumPopulated() {
        Door door = Door.build()
        assert door
        assert door.status == DoorStatus.OPEN
    }
}
