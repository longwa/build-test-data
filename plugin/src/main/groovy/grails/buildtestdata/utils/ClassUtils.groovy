package grails.buildtestdata.utils

import groovy.transform.CompileStatic
import org.springframework.beans.BeanUtils
import org.springframework.util.ReflectionUtils

import java.lang.reflect.Field
import java.lang.reflect.Method

@CompileStatic
class ClassUtils {
    private static final String PROPERTY_SET_PREFIX = "set";
    private static final String PROPERTY_GET_PREFIX = "get";


    /**
     * <p>Get a static property value, which has a public static getter or is just a public static field.</p>
     *
     * @param clazz The class to check for static property
     * @param name The property name
     * @return The value if there is one, or null if unset OR there is no such property
     */
    public static Object getStaticPropertyValue(Class<?> clazz, String name) {
        Method getter = BeanUtils.findDeclaredMethod(clazz, getGetterName(name), (Class[])null);
        try {
            if (getter != null) {
                ReflectionUtils.makeAccessible(getter);
                return getter.invoke(clazz);
            }
            return getStaticFieldValue(clazz, name);
        }
        catch (Exception ignored) {
            // ignored
        }
        return null;
    }
    /**
     * <p>Get a static field value.</p>
     *
     * @param clazz The class to check for static property
     * @param name The field name
     * @return The value if there is one, or null if unset OR there is no such field
     */
    public static Object getStaticFieldValue(Class<?> clazz, String name) {
        Field field = ReflectionUtils.findField(clazz, name);
        if (field != null) {
            ReflectionUtils.makeAccessible(field);
            try {
                return field.get(clazz);
            } catch (IllegalAccessException ignored) {}
        }
        return null;
    }

    /**
     * Calculate the name for a getter method to retrieve the specified property
     * @param propertyName
     * @return The name for the getter method for this property, if it were to exist, i.e. getConstraints
     */
    public static String getGetterName(String propertyName) {
        final String suffix = getSuffixForGetterOrSetter(propertyName);
        return PROPERTY_GET_PREFIX + suffix;
    }
    private static String getSuffixForGetterOrSetter(String propertyName) {
        final String suffix;
        if (propertyName.length() > 1 &&
            Character.isLowerCase(propertyName.charAt(0)) &&
            Character.isUpperCase(propertyName.charAt(1))) {
            suffix = propertyName;
        } else {
            suffix = "${Character.toUpperCase(propertyName.charAt(0))}${propertyName.substring(1)}"
        }
        return suffix;
    }
}
