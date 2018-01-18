package grails.buildtestdata.builders

import grails.buildtestdata.MockErrors
import grails.buildtestdata.handler.*
import grails.buildtestdata.utils.ClassUtils
import grails.gorm.validation.Constrained
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import grails.gorm.validation.PersistentEntityValidator
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper
import org.grails.datastore.mapping.model.MappingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class ConstraintsTestDataBuilder extends PogoTestDataBuilder {
    static final Logger log = LoggerFactory.getLogger(this)
    
    static List<String> CONSTRAINT_SORT_ORDER = [
        ConstrainedProperty.IN_LIST_CONSTRAINT, // most important
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
        ConstrainedProperty.MATCHES_CONSTRAINT,     // not implememnted, provide sample data
        ConstrainedProperty.VALIDATOR_CONSTRAINT,   // not implememnted, provide sample data
        ConstrainedProperty.BLANK_CONSTRAINT, // precluded by no '' default value applied in the nullable constraint handling
    ].reverse()
    
    static Map<String, ? extends ConstraintHandler> defaultHandlers = [
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
    
    // reverse so that when we compare, missing items are -1, then we are orders 0 -> n least to most important

    // TODO: filter to actual list for this class, or possibly each property value?
    Map<String, ? extends ConstraintHandler> handlers

    Collection<String> requiredPropertyNames

    ConstraintsTestDataBuilder(Class target) {
        super(target)
        this.requiredPropertyNames = findRequiredPropertyNames()
        this.handlers = new HashMap<String,? extends ConstraintHandler>(defaultHandlers)

    }

    def isRequiredConstrained(Constrained constrained) {
        boolean bindable = isBindable(constrained)
        boolean nullable = constrained.nullable
        !nullable && bindable
    }
    
    boolean isBindable(Constrained constrained){
        //TODO: Check if constraint is bindable 
        return true 
    }

    Map<String, ? extends Constrained> getConstraintsMap() {
        ClassUtils.getStaticPropertyValue(target,'constraintsMap') as Map<String, ? extends Constrained>
    }

    Collection<String> findRequiredPropertyNames() {
        Map<String,? extends Constrained> constraints = constraintsMap
        if(constraints){
            return constraints.keySet().findAll {
                isRequiredConstrained(constraints.get(it))
            }            
        }
        return []
    }

    
    @Override
    def build(BuildTestDataContext ctx) {
        Object instance = (Object) super.build(ctx)
        populateRequiredValues(instance, ctx)
        instance
    }

    void populateRequiredValues(Object instance, BuildTestDataContext ctx) {
        for (requiredPropertyName in requiredPropertyNames) {
            Constrained constrained = constraintsMap.get(requiredPropertyName)
            if(constrained instanceof ConstrainedProperty){
                if(!isSatisfied(instance,requiredPropertyName,(ConstrainedProperty) constrained)){
                    satisfyConstrained(instance, requiredPropertyName,(ConstrainedProperty) constrained,ctx)    
                }
                else if(!isBasicType(((ConstrainedProperty)constrained).propertyType)){
                    ctx.satisfyNested(instance,requiredPropertyName,((ConstrainedProperty)constrained).propertyType)
                }
            }
        }
    }
    
    boolean isSatisfied(Object instance,String propertyName,ConstrainedProperty constrainedProperty){
        def errors = new MockErrors(this)

        constrainedProperty.validate(instance, InvokerHelper.getProperty(instance,propertyName), errors)
        return !errors.hasErrors()
    }
    
    Object satisfyConstrained(Object instance, String propertyName, ConstrainedProperty constrained, BuildTestDataContext ctx) {
        return sortedConstraints(constrained.appliedConstraints).find { Constraint constraint ->
            if (log.debugEnabled) {
                log.debug "${target?.name}.${constraint?.name} constraint, field before adjustment: ${InvokerHelper.getProperty(instance,propertyName)}"
            }
            ConstraintHandler handler = handlers[constraint.name]
            if (handler) {
                handler.handle(instance, propertyName, constraint, constrained, ctx)
                if (log.debugEnabled) {
                    log.debug "${target?.name}.$propertyName field after adjustment for ${constraint?.name}: ${InvokerHelper.getProperty(instance,propertyName)}"
                }
            } else {
                if (log.warnEnabled) {
                    log.warn "Unable to find property generator handler for constraint ${constraint?.name}!"
                }
            }

            if (isSatisfied(instance, propertyName,constrained)) {
                return true
            }
        }
    }
    List<Constraint> sortedConstraints(Collection<Constraint> appliedConstraints) {
        return appliedConstraints.sort { a, b ->
            CONSTRAINT_SORT_ORDER.indexOf(b.name) <=> CONSTRAINT_SORT_ORDER.indexOf(a.name)
        }
    }
}
