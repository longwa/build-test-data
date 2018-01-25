package grails.buildtestdata.handler

import grails.gorm.validation.ConstrainedProperty
import groovy.transform.CompileStatic

@CompileStatic
class UniqueConstraintHandler extends AbstractHandler{

    @Override
    void handle(Object instance, String propertyName, ConstrainedProperty constrainedProperty) {
        // unique isn't supported, if the value we've got in there isn't valid by this point, throw an error letting
        // the user know why we're not passing
//        if (constrainedProperty.unique && !constrainedProperty?.validate(domain, domain."$propertyName", new ValidationErrors(this))) {
//            String error = "unique constraint support not implemented: property $propertyName of ${domain.class.name}"
//            throw new ConstraintHandlerException(error)
//        }
    }
}
