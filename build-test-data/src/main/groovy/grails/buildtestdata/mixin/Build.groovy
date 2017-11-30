package grails.buildtestdata.mixin

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface Build {
    Class[] value()
}
