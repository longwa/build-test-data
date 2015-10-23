package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
@Build([Car, Door])
class EnumUnitTests {
    @Test
    void testCarStatusEnumPopulated() {
        Car car = Car.build()
        assert car
        assert CarStatus.REVERSE == car.status
    }

    @Test
    void testDoorStatusEnumPopulated() {
        Door door = Door.build()
        assert door
        assert DoorStatus.OPEN == door.status
    }
}
