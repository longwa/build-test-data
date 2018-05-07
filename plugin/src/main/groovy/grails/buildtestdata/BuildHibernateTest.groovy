package grails.buildtestdata

import grails.buildtestdata.utils.MetaHelper
import grails.testing.spock.OnceBefore
import groovy.transform.CompileStatic

/**
 * Support build test data functionality for unit tests that extend HibernateSpec
 *
 * @since 3.3.1
 */
@CompileStatic
@SuppressWarnings("GroovyUnusedDeclaration")
trait BuildHibernateTest implements TestDataBuilder {
    /**
     * HibernateSpec must already override this method to provide the domains they are using
     */
    abstract List<Class> getDomainClasses()

    @OnceBefore
    void setupBuildMethods() {
        MetaHelper.addBuildMetaMethods(getDomainClasses().toArray(Class))
    }
}