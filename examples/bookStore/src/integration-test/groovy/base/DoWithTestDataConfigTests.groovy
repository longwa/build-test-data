package base

import config.Hotel
import grails.buildtestdata.TestDataBuilder
import grails.buildtestdata.TestDataConfigurationHolder
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Rollback
@Integration
class DoWithTestDataConfigTests extends Specification implements TestDataBuilder {

    @Override
    Closure doWithTestDataConfig() {{->
        testDataConfig {
            sampleData {
                'config.Hotel' {
                    name = {-> "Westin" }
                }
            }
        }
    }}

    void "Resetting uses default configuration"() {
        given:
        TestDataConfigurationHolder.reset()

        when:
        Hotel testHotel = build(Hotel)

        then:
        testHotel.name == "Motel 6"
    }

    void "doWithTestDataConfig overrides values in the default configuration"() {
        when:
        Hotel testHotel = build(Hotel)

        then:
        testHotel.name == "Westin"
    }

    void cleanupSpec() {
        TestDataConfigurationHolder.reset()
    }
}
