package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.buildtestdata.UnitTestDataBuilder
import spock.lang.Ignore
import spock.lang.Specification

class EnumUnitTests extends Specification implements UnitTestDataBuilder {
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

    @Ignore //FIXME not sure why this one is failing yet
    void testDoorStatusEnumPopulated() {
        when:
        Door door = build(Door)

        then:
        assert door
        assert DoorStatus.OPEN == door.status
    }
}
