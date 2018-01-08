package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.buildtestdata.TestDataBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class EnumTests extends Specification implements TestDataBuilder {
    void testCarStatusEnumPopulated() {
        when:
        Car car = build(Car)
        then:
        car
        car.status == CarStatus.REVERSE
    }

    void testDoorStatusEnumPopulated() {
        when:
        Door door = build(Door)
        then:
        door
        door.status == DoorStatus.OPEN
    }
}
