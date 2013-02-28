package grails.buildtestdata.handler

import grails.buildtestdata.MockErrors
import nl.flotsam.xeger.Xeger

public class MatchesConstraintHandler implements ConstraintHandler {
    public void handle(domain, propertyName, appliedConstraint, constrainedProperty = null, circularCheckList = null) {
        // If what we have already matches, we are good
        if ( !constrainedProperty?.validate(domain, domain."$propertyName", new MockErrors(this)) ) {
            Xeger generator = new Xeger(appliedConstraint.regex)
            domain."$propertyName" = generator.generate()
        }
    }
}
