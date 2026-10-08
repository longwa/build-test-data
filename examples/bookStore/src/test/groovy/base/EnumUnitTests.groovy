package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.buildtestdata.BuildDataUnitTest
import spock.lang.Specification

class EnumUnitTests extends Specification implements BuildDataUnitTest {
    void setupSpec() {
        mockDomains(Car, Door)
    }

    void testCarStatusEnumPopulated() {
        when:
        Car car = build(Car)

        then:
        assert car
        assert CarStatus.REVERSE == car.status
    }

    void testDoorStatusEnumPopulated() {
        when:
        Door door = build(Door)

        then:
        assert door
        assert DoorStatus.OPEN == door.status
    }
}
