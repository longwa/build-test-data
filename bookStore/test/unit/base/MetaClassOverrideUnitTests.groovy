package base

import enumtest.Car
import enumtest.CarStatus
import enumtest.Door
import enumtest.DoorStatus
import grails.buildtestdata.mixin.Build
import org.junit.Before
import org.junit.Test

@Build([Car])
class MetaClassOverrideUnitTests {

    @Before
    void setup() {
        // add a method to the metaClass that will be taken off without harming build
        Car.metaClass.honk {->
            return "Honk!"
        }
    }

    @Test
    void methodsAddedToMetaClassWork() {
        Car car = Car.build()

        assert car != null
        assert "Honk!" == car.honk()
    }

    @Test
    void testBuildMethodStillExistsAfterTeardownAndRebuild() {
        Car car = Car.build()

        assert car != null
        assert "Honk!" == car.honk()
    }

}
