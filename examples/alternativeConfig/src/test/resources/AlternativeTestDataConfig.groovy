testDataConfig {
    sampleData {
        // Simple class is in "alternativeconfig" package so we use a string in the builder
        'alternativeconfig.Simple' {
            name = "Alternative name"
        }
    }
}

// if you'd like to disable the build-test-data plugin in an environment, just set
// the "enabled" property to false

environments {
    production {
        testDataConfig {
            enabled = false
        }
    }
}