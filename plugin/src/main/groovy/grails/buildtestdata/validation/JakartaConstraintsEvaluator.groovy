package grails.buildtestdata.validation

import grails.buildtestdata.utils.Basics
import grails.gorm.validation.ConstrainedProperty
import grails.gorm.validation.Constraint
import grails.gorm.validation.DefaultConstrainedProperty
import grails.gorm.validation.exceptions.ValidationConfigurationException
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jakarta.validation.constraints.AssertFalse
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Negative
import jakarta.validation.constraints.NegativeOrZero
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry
import org.grails.datastore.gorm.validation.constraints.registry.DefaultConstraintRegistry
import org.grails.datastore.mapping.reflect.ClassPropertyFetcher
import org.grails.datastore.mapping.reflect.ClassUtils
import org.springframework.context.support.StaticMessageSource

import java.beans.Introspector
import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.math.RoundingMode
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime

/**
 * Translates the Jakarta Bean Validation annotations on a class (plus the common Hibernate Validator extras) into the
 * equivalent GORM constraints, so the regular constraint handlers can generate values that satisfy them.
 *
 * GORM validates an entity carrying Bean Validation annotations with Hibernate Validator instead of its own
 * {@link grails.gorm.validation.PersistentEntityValidator}, and that validator exposes no constraint metadata. The GORM
 * constraints are still the ones driving the schema, so both sets are merged: the GORM constraints are copied and the
 * annotations tighten them. The given constrained properties are never modified, the GORM validator may be using them.
 *
 * Only reference this class when {@link #isAvailable()} is true, it links against the Bean Validation API.
 */
@Slf4j
@CompileStatic
class JakartaConstraintsEvaluator {

    static final String CONSTRAINT_ANNOTATION_NAME = 'jakarta.validation.Constraint'

    /**
     * Constraint annotations without an equivalent GORM constraint, keyed by property name. Like the GORM `validator`
     * constraint there is no generic way to satisfy them, the user needs to provide sample data.
     */
    final Map<String, List<Class<? extends Annotation>>> unsupportedAnnotations = [:]

    /** @return whether the Bean Validation API is on the classpath */
    static boolean isAvailable() {
        ClassUtils.isPresent(CONSTRAINT_ANNOTATION_NAME)
    }

    /** @return the given validator when it is a Bean Validation one (GORM's GormValidatorAdapter is), otherwise null */
    static Validator asJakartaValidator(Object validator) {
        validator instanceof Validator ? (Validator) validator : null
    }

    /** @return whether the property's current value passes the Bean Validation constraints declared on it */
    static boolean isValidProperty(Object validator, Object instance, String propertyName) {
        Set<ConstraintViolation<Object>> violations = ((Validator) validator).validateProperty(instance, propertyName)
        if (violations) {
            log.debug("{}.{} rejected by Bean Validation: {}", instance.getClass().name, propertyName, violations*.message)
        }
        violations.isEmpty()
    }

    /** @return whether any property of the class (or its superclasses) carries a Bean Validation annotation */
    static boolean hasConstraintAnnotations(Class<?> clazz) {
        !findConstraintAnnotations(clazz).isEmpty()
    }

    /**
     * The constraint annotations on the fields and getters of the class and its superclasses, keyed by property name.
     * An annotation counts as a constraint when its type is meta-annotated with {@code jakarta.validation.Constraint},
     * which covers custom constraints too. Repeatable containers (such as {@code Size.List}) are expanded.
     */
    static Map<String, List<Annotation>> findConstraintAnnotations(Class<?> clazz) {
        Map<String, List<Annotation>> result = [:]
        for (Class<?> current = clazz; current != null && current != Object; current = current.superclass) {
            for (Field field in current.declaredFields) {
                if (!field.synthetic && !Modifier.isStatic(field.modifiers)) {
                    collectConstraintAnnotations(result, field.name, field.annotations)
                }
            }
            for (Method method in current.declaredMethods) {
                if (!method.synthetic && !Modifier.isStatic(method.modifiers) && method.parameterCount == 0) {
                    String propertyName = getterPropertyName(method)
                    if (propertyName) {
                        collectConstraintAnnotations(result, propertyName, method.annotations)
                    }
                }
            }
        }
        result
    }

    private static void collectConstraintAnnotations(Map<String, List<Annotation>> result, String propertyName, Annotation[] annotations) {
        for (Annotation annotation in annotations) {
            if (isConstraintAnnotation(annotation)) {
                result.computeIfAbsent(propertyName, { [] as List<Annotation> }).add(annotation)
            }
            else if (isRepeatableContainer(annotation)) {
                collectConstraintAnnotations(result, propertyName, containedAnnotations(annotation))
            }
        }
    }

    static boolean isConstraintAnnotation(Annotation annotation) {
        isConstraintAnnotationType(annotation.annotationType())
    }

    static boolean isConstraintAnnotationType(Class<? extends Annotation> type) {
        type.annotations.any { Annotation meta -> meta.annotationType().name == CONSTRAINT_ANNOTATION_NAME }
    }

    /** Repeated constraints are read back as their nested container annotation, e.g. {@code Pattern.List} */
    private static boolean isRepeatableContainer(Annotation annotation) {
        Class<?> enclosing = annotation.annotationType().enclosingClass
        enclosing != null && enclosing.isAnnotation() && isConstraintAnnotationType(enclosing as Class<? extends Annotation>)
    }

    private static Annotation[] containedAnnotations(Annotation container) {
        try {
            Object value = container.annotationType().getMethod('value').invoke(container)
            return value instanceof Annotation[] ? (Annotation[]) value : new Annotation[0]
        }
        catch (ReflectiveOperationException ignored) {
            return new Annotation[0]
        }
    }

    private static String getterPropertyName(Method method) {
        String name = method.name
        if (name.startsWith('get') && name.length() > 3 && method.returnType != Void.TYPE) {
            return Introspector.decapitalize(name.substring(3))
        }
        if (name.startsWith('is') && name.length() > 2 && (method.returnType == Boolean.TYPE || method.returnType == Boolean)) {
            return Introspector.decapitalize(name.substring(2))
        }
        null
    }

    /**
     * Merges the Bean Validation annotations of the class into a copy of the given GORM constraints.
     *
     * @param owningClass the class carrying the annotations
     * @param constraints the GORM constraints of the class, left untouched
     * @param registry the registry to build the constraints with, defaults to the one of the given constraints
     * @return the given map when the class has no annotations, otherwise a new map with the merged constraints. A
     * property annotated with {@code @Null} is left out so it is never populated.
     */
    Map<String, ConstrainedProperty> apply(Class<?> owningClass, Map<String, ConstrainedProperty> constraints, ConstraintRegistry registry = null) {
        Map<String, List<Annotation>> annotationsByProperty = findConstraintAnnotations(owningClass)
        if (annotationsByProperty.isEmpty()) {
            return constraints
        }

        Map<String, ConstrainedProperty> merged = new LinkedHashMap<String, ConstrainedProperty>(constraints ?: [:])
        annotationsByProperty.each { String propertyName, List<Annotation> annotations ->
            if (annotations.any { Annotation annotation -> annotation instanceof Null }) {
                log.debug("{}.{} is annotated with @Null, it will not be populated", owningClass.name, propertyName)
                merged.remove(propertyName)
                return
            }

            ConstrainedProperty existing = constraints?.get(propertyName)
            ConstraintRegistry targetRegistry = registry ?: registryOf(existing)
            DefaultConstrainedProperty constrained
            if (existing) {
                constrained = copy(existing, targetRegistry)
            }
            else {
                Class<?> propertyType = ClassPropertyFetcher.getPropertyType(owningClass, propertyName)
                if (propertyType == null) {
                    log.debug("{}.{} is not a property, ignoring its constraint annotations", owningClass.name, propertyName)
                    return
                }
                constrained = new DefaultConstrainedProperty(owningClass, propertyName, propertyType, targetRegistry)
                constrained.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, true)
            }

            for (Annotation annotation in annotations) {
                applyAnnotation(constrained, annotation)
            }
            merged.put(propertyName, constrained)
        }
        merged
    }

    /**
     * A fresh constrained property with the same constraints and metadata, so that tightening it leaves the original
     * (which the GORM validator may be using) as it is.
     */
    static DefaultConstrainedProperty copy(ConstrainedProperty source, ConstraintRegistry registry) {
        DefaultConstrainedProperty target = new DefaultConstrainedProperty(source.owner, source.propertyName, source.propertyType, registry)
        for (Constraint constraint in source.appliedConstraints) {
            try {
                target.applyConstraint(constraint.name, constraint.parameter)
            }
            catch (Exception e) {
                log.warn("Unable to copy the {} constraint of {}.{}: {}", constraint.name, source.owner.name, source.propertyName, e.message)
            }
        }
        if (source instanceof DefaultConstrainedProperty) {
            DefaultConstrainedProperty defaults = (DefaultConstrainedProperty) source
            defaults.metaConstraints.each { String name, Object value -> target.addMetaConstraint(name, value) }
            target.order = defaults.order
            target.displayType = defaults.displayType
            target.editable = defaults.editable
            target.format = defaults.format
            target.widget = defaults.widget
            target.password = defaults.password
            target.attributes = defaults.attributes
        }
        target
    }

    /**
     * The registry a constrained property was built with. GORM keeps it in a protected field without an accessor,
     * fall back to a registry with the standard constraints when it can't be read.
     */
    @CompileDynamic
    static ConstraintRegistry registryOf(ConstrainedProperty constrained) {
        try {
            ConstraintRegistry registry = constrained?.constraintRegistry as ConstraintRegistry
            if (registry) {
                return registry
            }
        }
        catch (MissingPropertyException ignored) {
            // not a DefaultConstrainedProperty
        }
        new DefaultConstraintRegistry(new StaticMessageSource())
    }

    /**
     * Applies the GORM constraint(s) equivalent to the annotation, tightening rather than replacing bounds the GORM
     * constraints already impose. Composed custom constraints are handled through the constraints composing them.
     */
    void applyAnnotation(DefaultConstrainedProperty constrained, Annotation annotation) {
        switch (annotation) {
            case NotNull:
                applyIfSupported(constrained, ConstrainedProperty.NULLABLE_CONSTRAINT, false)
                break
            case NotBlank:
                applyIfSupported(constrained, ConstrainedProperty.NULLABLE_CONSTRAINT, false)
                applyIfSupported(constrained, ConstrainedProperty.BLANK_CONSTRAINT, false)
                break
            case NotEmpty:
                applyIfSupported(constrained, ConstrainedProperty.NULLABLE_CONSTRAINT, false)
                tightenMinSize(constrained, 1)
                break
            case Size:
                Size size = (Size) annotation
                if (size.min() > 0) {
                    tightenMinSize(constrained, size.min())
                }
                if (size.max() < Integer.MAX_VALUE) {
                    tightenMaxSize(constrained, size.max())
                }
                break
            case Min:
                tightenMin(constrained, ((Min) annotation).value())
                break
            case Max:
                tightenMax(constrained, ((Max) annotation).value())
                break
            case DecimalMin:
                DecimalMin decimalMin = (DecimalMin) annotation
                BigDecimal min = new BigDecimal(decimalMin.value())
                tightenMin(constrained, decimalMin.inclusive() ? min : min.add(min.ulp()))
                break
            case DecimalMax:
                DecimalMax decimalMax = (DecimalMax) annotation
                BigDecimal max = new BigDecimal(decimalMax.value())
                tightenMax(constrained, decimalMax.inclusive() ? max : max.subtract(max.ulp()))
                break
            case Positive:
                tightenMin(constrained, 1)
                break
            case PositiveOrZero:
                tightenMin(constrained, 0)
                break
            case Negative:
                tightenMax(constrained, -1)
                break
            case NegativeOrZero:
                tightenMax(constrained, 0)
                break
            case Digits:
                // the generated values are whole numbers, only the number of integer digits needs limiting
                BigDecimal limit = BigDecimal.TEN.pow(((Digits) annotation).integer()).subtract(BigDecimal.ONE)
                tightenMax(constrained, limit)
                tightenMin(constrained, limit.negate())
                break
            case Email:
                applyIfSupported(constrained, ConstrainedProperty.EMAIL_CONSTRAINT, true)
                Email email = (Email) annotation
                if (email.regexp() != '.*') {
                    applyMatches(constrained, withInlineFlags(email.regexp(), email.flags()))
                }
                break
            case Pattern:
                Pattern pattern = (Pattern) annotation
                applyMatches(constrained, withInlineFlags(pattern.regexp(), pattern.flags()))
                break
            case AssertTrue:
                applyIfSupported(constrained, ConstrainedProperty.IN_LIST_CONSTRAINT, [true])
                break
            case AssertFalse:
                applyIfSupported(constrained, ConstrainedProperty.IN_LIST_CONSTRAINT, [false])
                break
            case Past:
            case PastOrPresent:
                tightenTemporalMax(constrained, annotation)
                break
            case Future:
            case FutureOrPresent:
                tightenTemporalMin(constrained, annotation)
                break
            default:
                if (!applyHibernateValidatorAnnotation(constrained, annotation) && !applyComposedAnnotation(constrained, annotation)) {
                    log.debug("No equivalent GORM constraint for @{} on {}.{}, provide sample data if the generated value is rejected",
                        annotation.annotationType().simpleName, constrained.owner.name, constrained.propertyName)
                    unsupportedAnnotations.computeIfAbsent(constrained.propertyName, { [] as List<Class<? extends Annotation>> })
                        .add(annotation.annotationType())
                }
        }
    }

    /**
     * The Hibernate Validator specific annotations with a GORM equivalent, matched by name so the plugin doesn't need
     * Hibernate Validator on its classpath
     */
    private boolean applyHibernateValidatorAnnotation(DefaultConstrainedProperty constrained, Annotation annotation) {
        switch (annotation.annotationType().name) {
            case 'org.hibernate.validator.constraints.Length':
                int min = annotationMember(annotation, 'min') as int
                int max = annotationMember(annotation, 'max') as int
                if (min > 0) {
                    tightenMinSize(constrained, min)
                }
                if (max < Integer.MAX_VALUE) {
                    tightenMaxSize(constrained, max)
                }
                return true
            case 'org.hibernate.validator.constraints.Range':
                tightenMin(constrained, annotationMember(annotation, 'min') as long)
                tightenMax(constrained, annotationMember(annotation, 'max') as long)
                return true
            case 'org.hibernate.validator.constraints.URL':
                return applyIfSupported(constrained, ConstrainedProperty.URL_CONSTRAINT, true)
            case 'org.hibernate.validator.constraints.CreditCardNumber':
                return applyIfSupported(constrained, ConstrainedProperty.CREDIT_CARD_CONSTRAINT, true)
            default:
                return false
        }
    }

    /** A custom constraint composed of other constraints (meta-annotated with them) is satisfied through those */
    private boolean applyComposedAnnotation(DefaultConstrainedProperty constrained, Annotation annotation) {
        List<Annotation> composing = []
        for (Annotation meta in annotation.annotationType().annotations) {
            if (isConstraintAnnotation(meta)) {
                composing << meta
            }
            else if (isRepeatableContainer(meta)) {
                composing.addAll(containedAnnotations(meta))
            }
        }
        for (Annotation meta in composing) {
            applyAnnotation(constrained, meta)
        }
        !composing.isEmpty()
    }

    private static Object annotationMember(Annotation annotation, String name) {
        annotation.annotationType().getMethod(name).invoke(annotation)
    }

    /** The embedded flag expression letters of java.util.regex.Pattern, CANON_EQ has none and is left out */
    static final Map<Pattern.Flag, String> INLINE_FLAGS = [
        (Pattern.Flag.UNIX_LINES): 'd',
        (Pattern.Flag.CASE_INSENSITIVE): 'i',
        (Pattern.Flag.COMMENTS): 'x',
        (Pattern.Flag.MULTILINE): 'm',
        (Pattern.Flag.DOTALL): 's',
        (Pattern.Flag.UNICODE_CASE): 'u',
    ].asImmutable()

    /**
     * The GORM matches constraint takes a bare regular expression, so the annotation's flags are turned into the
     * equivalent embedded flag expression, e.g. {@code (?i)} for CASE_INSENSITIVE, which makes the constraint match
     * exactly what the annotation does
     */
    static String withInlineFlags(String regexp, Pattern.Flag[] flags) {
        String letters = flags.collect { Pattern.Flag flag -> INLINE_FLAGS[flag] }.findAll().join('')
        letters ? "(?${letters})${regexp}".toString() : regexp
    }

    private void applyMatches(DefaultConstrainedProperty constrained, String regexp) {
        if (constrained.hasAppliedConstraint(ConstrainedProperty.MATCHES_CONSTRAINT)) {
            log.debug("{}.{} already has a matches constraint, keeping it over the annotation's {}",
                constrained.owner.name, constrained.propertyName, regexp)
            return
        }
        applyIfSupported(constrained, ConstrainedProperty.MATCHES_CONSTRAINT, regexp)
    }

    private void tightenMinSize(DefaultConstrainedProperty constrained, int minSize) {
        Integer current = constrained.minSize
        if (current == null || minSize > current) {
            applyIfSupported(constrained, ConstrainedProperty.MIN_SIZE_CONSTRAINT, minSize)
        }
    }

    private void tightenMaxSize(DefaultConstrainedProperty constrained, int maxSize) {
        Integer current = constrained.maxSize
        if (current == null || maxSize < current) {
            applyIfSupported(constrained, ConstrainedProperty.MAX_SIZE_CONSTRAINT, maxSize)
        }
    }

    private void tightenMin(DefaultConstrainedProperty constrained, Number min) {
        Comparable value = convertNumber(min, constrained.propertyType, RoundingMode.CEILING)
        if (value != null && (constrained.min == null || value.compareTo(constrained.min) > 0)) {
            applyIfSupported(constrained, ConstrainedProperty.MIN_CONSTRAINT, value)
        }
    }

    private void tightenMax(DefaultConstrainedProperty constrained, Number max) {
        Comparable value = convertNumber(max, constrained.propertyType, RoundingMode.FLOOR)
        if (value != null && (constrained.max == null || value.compareTo(constrained.max) < 0)) {
            applyIfSupported(constrained, ConstrainedProperty.MAX_CONSTRAINT, value)
        }
    }

    private void tightenTemporalMax(DefaultConstrainedProperty constrained, Annotation annotation) {
        Comparable value = temporalFromNow(constrained.propertyType, -1)
        if (value == null) {
            unsupportedTemporal(constrained, annotation)
        }
        else if (constrained.max == null || value.compareTo(constrained.max) < 0) {
            applyIfSupported(constrained, ConstrainedProperty.MAX_CONSTRAINT, value)
        }
    }

    private void tightenTemporalMin(DefaultConstrainedProperty constrained, Annotation annotation) {
        Comparable value = temporalFromNow(constrained.propertyType, 1)
        if (value == null) {
            unsupportedTemporal(constrained, annotation)
        }
        else if (constrained.min == null || value.compareTo(constrained.min) > 0) {
            applyIfSupported(constrained, ConstrainedProperty.MIN_CONSTRAINT, value)
        }
    }

    private void unsupportedTemporal(DefaultConstrainedProperty constrained, Annotation annotation) {
        log.debug("@{} is not supported on a {} property ({}.{}), provide sample data if the generated value is rejected",
            annotation.annotationType().simpleName, constrained.propertyType.name, constrained.owner.name, constrained.propertyName)
        unsupportedAnnotations.computeIfAbsent(constrained.propertyName, { [] as List<Class<? extends Annotation>> })
            .add(annotation.annotationType())
    }

    private boolean applyIfSupported(DefaultConstrainedProperty constrained, String constraintName, Object value) {
        if (!constrained.supportsContraint(constraintName)) {
            log.debug("The {} constraint does not apply to {}.{} ({}), skipping it",
                constraintName, constrained.owner.name, constrained.propertyName, constrained.propertyType.name)
            return false
        }
        try {
            constrained.applyConstraint(constraintName, value)
            return true
        }
        catch (ValidationConfigurationException e) {
            log.warn("Unable to apply the {} constraint to {}.{}: {}", constraintName, constrained.owner.name, constrained.propertyName, e.message)
            return false
        }
    }

    /**
     * Converts a bound to the property's own type, which the GORM min/max constraints require. Returns null when the
     * property isn't numeric or the bound doesn't fit in the type.
     */
    static Comparable convertNumber(Number value, Class<?> propertyType, RoundingMode rounding) {
        Class<?> type = Basics.boxPrimitive(propertyType)
        BigDecimal decimal = value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString())
        try {
            if (type == BigDecimal) {
                return decimal
            }
            if (type == Double) {
                return decimal.doubleValue()
            }
            if (type == Float) {
                return decimal.floatValue()
            }
            BigDecimal whole = decimal.setScale(0, rounding)
            if (type == Integer) {
                return whole.intValueExact()
            }
            if (type == Long) {
                return whole.longValueExact()
            }
            if (type == Short) {
                return whole.shortValueExact()
            }
            if (type == Byte) {
                return whole.byteValueExact()
            }
            if (type == BigInteger) {
                return whole.toBigIntegerExact()
            }
        }
        catch (ArithmeticException ignored) {
            // the bound doesn't fit in the property type
        }
        null
    }

    /**
     * A value of the property's temporal type one unit (a day, or a year/month for Year/YearMonth) away from now, in
     * the direction of the offset's sign. Null for types where "past" and "future" have no meaning, such as LocalTime.
     */
    static Comparable temporalFromNow(Class<?> type, int offset) {
        ZonedDateTime now = ZonedDateTime.now().plusDays(offset)
        if (type == Date) {
            return Date.from(now.toInstant())
        }
        if (type == Timestamp) {
            return Timestamp.from(now.toInstant())
        }
        if (type == java.sql.Date) {
            return new java.sql.Date(now.toInstant().toEpochMilli())
        }
        if (type == Instant) {
            return now.toInstant()
        }
        if (type == LocalDate) {
            return now.toLocalDate()
        }
        if (type == LocalDateTime) {
            return now.toLocalDateTime()
        }
        if (type == ZonedDateTime) {
            return now
        }
        if (type == OffsetDateTime) {
            return now.toOffsetDateTime()
        }
        if (type == Year) {
            return Year.now().plusYears(offset)
        }
        if (type == YearMonth) {
            return YearMonth.now().plusMonths(offset)
        }
        null
    }
}
