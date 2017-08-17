package grails.buildtestdata.testing

import grails.testing.gorm.DomainUnitTest
import groovy.transform.CompileStatic

import java.lang.reflect.ParameterizedType

@CompileStatic
trait BuildDomainUnitTest<T> implements BuildDataTest{
    private T domainInstance
    private static Class<T> domainClass

    Class<?>[] getDomainClassesToMock() {
        [getDomainUnderTest()].toArray(Class)
    }

    /**
     * @return An instance of the domain class
     */
    T getDomain() {
        if (domainInstance == null) {
            domainInstance = (T) getDomainUnderTest().invokeMethod("build",null)
        }
        domainInstance
    }

    private Class<T> getDomainUnderTest() {
        if (domainClass == null) {
            ParameterizedType parameterizedType = (ParameterizedType)getClass().genericInterfaces.find { genericInterface ->
                genericInterface instanceof ParameterizedType &&
                    BuildDomainUnitTest.isAssignableFrom((Class)((ParameterizedType)genericInterface).rawType)
            }

            domainClass = (Class<T>)parameterizedType?.actualTypeArguments[0]
        }
        domainClass
    }
}