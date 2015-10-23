[![Build Status](https://api.travis-ci.org/longwa/build-test-data.png?branch=master)](https://travis-ci.org/longwa/build-test-data)

## The Build Test Data Grails Plugin 

Creating maintainable test data is hard.  Often an entire object graph needs to be created to support the instantiation of a single domain object.  This leads to either the cutting and pasting of that creation code, or relying on a canned set of objects that we've grown over time and maintained as the domain objects change.  After a while, adding just one more Widget to that set of canned data ends up breaking tests just about every time.

There has to be a better solution, right?  

Yep!  Due to the power and the glory of Grails, we have a lot of metadata at our fingertips about those domain objects.  We know what constraints we've placed on our objects, and which objects depend on other objects to live.

Using this additional information, we've created a grails plugin that makes it easy to just provide those values that you want to exercise under test and not worry about the rest of the object graph that you need to create just to instantiate your domain objects.

This plugin is focused on creating test data for integration testing.  As of Grails 2.0.0, build-test-data also supports unit tests through annotations and mixins. 

Once installed, all you have to do is call the new "build" method on your domain class and you'll be given a valid instance with all of the required constraints given values. 
```groovy
def a = Author.build()
```
In a unit test, you'll just use an annotation to let it know what domain class you'd like to build 
```groovy
import grails.buildtestdata.mixin.Build

@Build(Author)
class AuthorUnitTests {

    void testAuthorStuff() {
        def author = Author.build()
        ...
    }

}
```

### Plugin Objectives 

- The definition of the domain objects under test should be next to the test code, this improves test comprehension.
- You should only need to create those fields and objects that are pertinent to the test.  Other test setup is noise that obfuscates the meaning of the test.
- Tests should not be dependent on other tests, only on the code under test.  Therefore, the same test data should not be used by multiple tests, this creates a strong coupling and leads to test fragility.
- Changes to domain objects that do not affect the the code under test should not break the test.


* [Features](http://github.com/tednaleid/build-test-data/wiki/Features)
* [Overview Presentation](http://www.slideshare.net/longwa/grails-buildtestdata-plugin-1723277)
* [Installation](http://github.com/longwa/build-test-data/wiki/Installation)
* [Basic Usage](http://github.com/longwa/build-test-data/wiki/BasicUsage)
* [Unit Test Support](http://github.com/longwa/build-test-data/wiki/UnitTestSupport) - as of Grails 2.0
* [Sample Code](http://github.com/longwa/build-test-data/wiki/SampleCode)
* [Build Test Data Demo application](https://github.com/longwa/build-test-data/tree/master/bookStore)
* [Fixtures Plugin + Build Test Data Demo application](https://github.com/stokito/grails-fixtures-demo)
* [Configuration using TestDataConfig](http://github.com/longwa/build-test-data/wiki/TestDataConfig)
* [Other Test Data Creation Patterns](http://github.com/longwa/build-test-data/wiki/OtherTestDataCreationStrengthsWeaknesses)
* [Release Notes/Upgrade Instructions](http://github.com/longwa/build-test-data/wiki/ReleaseNotes)
