package grails.buildtestdata.handler

import grails.buildtestdata.CircularCheckList
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

/**
 * Common interface for all build constraint handlers
 */
@CompileStatic
interface ConstraintHandler {

    // Constraints that we know about
    static List<String> CONSTRAINT_SORT_ORDER = [
        ConstrainedProperty.IN_LIST_CONSTRAINT,
        ConstrainedProperty.EMAIL_CONSTRAINT,
        ConstrainedProperty.CREDIT_CARD_CONSTRAINT,
        ConstrainedProperty.URL_CONSTRAINT,
        ConstrainedProperty.RANGE_CONSTRAINT,
        ConstrainedProperty.SCALE_CONSTRAINT,
        ConstrainedProperty.SIZE_CONSTRAINT,
        ConstrainedProperty.MAX_CONSTRAINT,
        ConstrainedProperty.MIN_CONSTRAINT,
        ConstrainedProperty.MIN_SIZE_CONSTRAINT,
        ConstrainedProperty.MAX_SIZE_CONSTRAINT,
        ConstrainedProperty.MATCHES_CONSTRAINT,
        ConstrainedProperty.VALIDATOR_CONSTRAINT,   // not implememnted, provide sample data
        ConstrainedProperty.BLANK_CONSTRAINT,       // precluded by no '' default value applied in the nullable constraint handling
    ].reverse()

    // Registered handlers
    static Map<String, ConstraintHandler> handlers = [
        (ConstrainedProperty.MIN_SIZE_CONSTRAINT)   : new MinSizeConstraintHandler(),
        (ConstrainedProperty.MAX_SIZE_CONSTRAINT)   : new MaxSizeConstraintHandler(),
        (ConstrainedProperty.IN_LIST_CONSTRAINT)    : new InListConstraintHandler(),
        (ConstrainedProperty.CREDIT_CARD_CONSTRAINT): new CreditCardConstraintHandler(),
        (ConstrainedProperty.EMAIL_CONSTRAINT)      : new EmailConstraintHandler(),
        (ConstrainedProperty.URL_CONSTRAINT)        : new UrlConstraintHandler(),
        (ConstrainedProperty.RANGE_CONSTRAINT)      : new RangeConstraintHandler(),
        (ConstrainedProperty.SIZE_CONSTRAINT)       : new SizeConstraintHandler(),
        (ConstrainedProperty.MIN_CONSTRAINT)        : new MinConstraintHandler(),
        (ConstrainedProperty.MAX_CONSTRAINT)        : new MaxConstraintHandler(),
        (ConstrainedProperty.NULLABLE_CONSTRAINT)   : new NullableConstraintHandler(),
        (ConstrainedProperty.MATCHES_CONSTRAINT)    : new MatchesConstraintHandler(),
        (ConstrainedProperty.BLANK_CONSTRAINT)      : new BlankConstraintHandler(),
        (ConstrainedProperty.VALIDATOR_CONSTRAINT)  : new ValidatorConstraintHandler()
    ]

    /**
     * Satisfy the given constraint for this domain object and property
     *
     * @param domain
     * @param propertyName
     * @param appliedConstraint
     * @param constrainedProperty
     * @param circularCheckList
     */
    void handle(GormEntity domain, String propertyName, Constraint appliedConstraint, ConstrainedProperty constrainedProperty, CircularCheckList circularCheckList)
}
