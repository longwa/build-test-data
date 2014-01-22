package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.buildtestdata.mixin.Build
import org.junit.Test

@Build([Car, Door])
class EnumUnitTests {
    @Test
    void testCarStatusEnumPopulated() {
        Car car = Car.build()
        assertNotNull car
        assertEquals CarStatus.REVERSE, car.status
    }

    @Test
    void testDoorStatusEnumPopulated() {
        Door door = Door.build()
        assertNotNull door
        assertEquals DoorStatus.OPEN, door.status
    }
}
