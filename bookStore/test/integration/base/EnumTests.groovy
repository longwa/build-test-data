package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus

class EnumTests extends GroovyTestCase {
    void testCarStatusEnumPopulated() {
        Car car = Car.build()
        assertNotNull car
        assertEquals CarStatus.REVERSE, car.status
    }

    void testDoorStatusEnumPopulated() {
        Door door = Door.build()
        assertNotNull door
        assertEquals DoorStatus.OPEN, door.status
    }
}
